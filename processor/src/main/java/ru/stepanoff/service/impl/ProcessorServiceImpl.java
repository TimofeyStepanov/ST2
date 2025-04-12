package ru.stepanoff.service.impl;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.stepanoff.client.MetricsClient;
import ru.stepanoff.client.ProducerClient;
import ru.stepanoff.client.RedisClient;
import ru.stepanoff.dto.ProcessorDTO;
import ru.stepanoff.dto.ProcessorResponse;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerDTO;
import ru.stepanoff.repository.StateRepository;
import ru.stepanoff.service.ProcessorService;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessorServiceImpl implements ProcessorService {
    /**
     * Словарь, где ключ - название топика, а значение - функцуия, которая принимает на вход словарь (названия полей для вставки и их значения), а возвращает готовый сгенерированный sql скрипт для вставки данных
     */
    private final Map<String, Function<Map<String, String>, String>> topicNameAndItsInsertRequestCreator = new ConcurrentHashMap<>();
    /**
     * Словарь, где ключ - название топика, а значение - функцуия, которая принимает на вход словарь (названия полей для вставки и их значения), а возвращает готовый сгенерированный sql скрипт для получения данных
     */
    private final Map<String, Function<Map<String, String>, String>> topicNameAndItsSelectRequestCreator = new ConcurrentHashMap<>();


    private final StateRepository stateRepository;
    private final ProducerClient producerClient;
    private final RedisClient<String, List<String>> redisClient;
    private final MetricsClient metricsClient;

    private Optional<ProcessorRuleDTO> optionalCurrentProcessorRule = Optional.empty();
    private volatile boolean ddlScriptsProcessedCorrectly = false;

    @Override
    public Mono<ProcessorResponse> process(ProcessorDTO processorDTO) {
        if (!ddlScriptsProcessedCorrectly) {
            return Mono.error(Status.UNAVAILABLE.withDescription("DDL скрипты отработали неверно").asRuntimeException());
        }

        Optional<String> optionalInsertRequest = createInsertRequest(processorDTO);
        Optional<String> optionalSelectRequest = createSelectRequest(processorDTO);
        if (optionalInsertRequest.isEmpty()) {
            return Mono.error(Status.INVALID_ARGUMENT.withDescription("Нет запроса на вставку данных для топика '%s'".formatted(processorDTO.getTopicAlias())).asRuntimeException());
        }
        if (optionalSelectRequest.isEmpty()) {
            return Mono.error(Status.INVALID_ARGUMENT.withDescription("Нет запроса на получение данных для топика '%s'".formatted(processorDTO.getTopicAlias())).asRuntimeException());
        }

        String insertRequest = optionalInsertRequest.get();
        String selectRequest = optionalSelectRequest.get();
        try {
            stateRepository.createSqlQuery(insertRequest, String.class);
            Optional<List<String>> cacheResult = redisClient.get(selectRequest);
            if (cacheResult.isPresent()) {
                log.debug("Получил из кеша значение для ключа: " + selectRequest);
                ProducerDTO producerDTO = new ProducerDTO();
                producerDTO.setMessages(cacheResult.get());
                return producerClient
                        .sendToProducerAsync(producerDTO)
                        .map(producerResponse -> new ProcessorResponse())
                        .onErrorResume(throwable -> Mono.error(Status.INVALID_ARGUMENT.withDescription("Ошибка при отправке сообщения: " + throwable.getMessage()).asRuntimeException()));
            }

            List<String> selectResults = stateRepository.createSqlQuery(selectRequest, String.class);
            if (selectResults.isEmpty()) {
                return Mono.just(new ProcessorResponse());
            }
            redisClient.save(selectRequest, selectResults);

            ProducerDTO producerDTO = new ProducerDTO();
            producerDTO.setMessages(selectResults);
            return producerClient
                    .sendToProducerAsync(producerDTO)
                    .map(producerResponse -> {
                        log.debug("Ответ от продюсера: {}", producerResponse);
                        metricsClient.incrementNumberOfSentMessagesToProducer();
                        return new ProcessorResponse();
                    })
                    .onErrorResume(throwable -> Mono.error(Status.UNAVAILABLE.withDescription("Ошибка при отправке сообщения продюсеру: " + throwable.getMessage()).asRuntimeException()));
        } catch (SQLException e) {
            return Mono.error(Status.INVALID_ARGUMENT.withDescription("Ошибка: " + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateProcessorRule(ProcessorRuleDTO processorRule) {
        if (!needToUpdateRule(processorRule)) {
            return;
        }

        try {
            if (needToMakeDDLScripts(processorRule)) {
                for (String ddlScript : processorRule.getScriptsDDL()) {
                    stateRepository.createSqlQuery(ddlScript, String.class);
                }
            }

            topicNameAndItsInsertRequestCreator.clear();
            topicNameAndItsSelectRequestCreator.clear();

            updateInsertRequests(processorRule);
            updateSelectRequests(processorRule);

            optionalCurrentProcessorRule = Optional.of(processorRule);
            ddlScriptsProcessedCorrectly = true;
        } catch (IllegalArgumentException | SQLException e) {
            ddlScriptsProcessedCorrectly = false;
            log.error("Ошибка при обновлении правила: {}", e.getMessage());
        } finally {
            metricsClient.sendDDLScriptsInfo(ddlScriptsProcessedCorrectly);
        }
    }

    private boolean needToUpdateRule(ProcessorRuleDTO newProcessorRule) {
        if (optionalCurrentProcessorRule.isEmpty() || !ddlScriptsProcessedCorrectly) {
            return true;
        }

        ProcessorRuleDTO currentProcessorRule = optionalCurrentProcessorRule.get();
        if (currentProcessorRule.equals(newProcessorRule)) {
            return false;
        }
        return newProcessorRule.getCreatedAt() > currentProcessorRule.getCreatedAt();
    }

    private boolean needToMakeDDLScripts(ProcessorRuleDTO newProcessorRule) {
        if (optionalCurrentProcessorRule.isEmpty()) {
            return true;
        }

        ProcessorRuleDTO currentProcessorRule = optionalCurrentProcessorRule.get();
        Set<String> currentDDLScripts = new HashSet<>(currentProcessorRule.getScriptsDDL());
        Set<String> newDDLScripts = new HashSet<>(newProcessorRule.getScriptsDDL());
        return !currentDDLScripts.equals(newDDLScripts);
    }

    private void updateInsertRequests(ProcessorRuleDTO processorRule) {
        processorRule.getTopicAliasAndItsInfo().forEach((topicAlias, topicInfo) -> {
            String insertRequest = topicInfo.getInsertRequest();
            String fieldsNamesToInsertFromRequest = insertRequest.substring(insertRequest.indexOf('(') + 1, insertRequest.indexOf(')') );
            List<String> fieldNameToInsertList = Arrays.stream(fieldsNamesToInsertFromRequest.split(","))
                    .map(String::trim)
                    .toList();

            Function<Map<String, String>, String> insertRequestCreator = fieldNameAndItsValue -> {
                String insertRequest1 = topicInfo.getInsertRequest();
                for (String fieldNameToInsert : fieldNameToInsertList) {
                    insertRequest1 = insertRequest1.replaceFirst("\\?", fieldNameAndItsValue.get(fieldNameToInsert));
                }
                return insertRequest1.replace('"', '\'');
            };
            topicNameAndItsInsertRequestCreator.put(topicAlias, insertRequestCreator);
        });
    }

    private void updateSelectRequests(ProcessorRuleDTO processorRule) {
        Pattern pattern = Pattern.compile("\\w*.(\\w*)\\s*=\\s*'?\\?'?");
        processorRule.getTopicAliasAndItsInfo().forEach((topicAlias, topicInfo) -> {
            List<String> aliasConcatenatedWithFieldNameToSelectList = new ArrayList<>();
            Matcher matcher = pattern.matcher(topicInfo.getSelectRequest());
            while (matcher.find()) {
                aliasConcatenatedWithFieldNameToSelectList.add(matcher.group(1).trim());
            }

            Function<Map<String, String>, String> selectRequestCreator = fieldNameAndItsValue -> {
                String selectRequest = topicInfo.getSelectRequest();
                for (String aliasConcatenatedWithFieldName : aliasConcatenatedWithFieldNameToSelectList) {
                    selectRequest = selectRequest.replace("?", fieldNameAndItsValue.get(aliasConcatenatedWithFieldName));
                }
                return selectRequest.replace('"', '\'');
            };
            topicNameAndItsSelectRequestCreator.put(topicAlias, selectRequestCreator);
        });
    }

    private Optional<String> createInsertRequest(ProcessorDTO processorRule) {
        Function<Map<String, String>, String> functionToCreateInsertSqlRequest = topicNameAndItsInsertRequestCreator.get(processorRule.getTopicAlias());
        if (functionToCreateInsertSqlRequest == null) {
            return Optional.empty();
        }
        return Optional.of(functionToCreateInsertSqlRequest.apply(processorRule.getFieldNameAndItsValue()));
    }

    private Optional<String> createSelectRequest(ProcessorDTO processorRule) {
        Function<Map<String, String>, String> functionToCreateSelectSqlRequest = topicNameAndItsSelectRequestCreator.get(processorRule.getTopicAlias());
        if (functionToCreateSelectSqlRequest == null) {
            return Optional.empty();
        }
        return Optional.of(functionToCreateSelectSqlRequest.apply(processorRule.getFieldNameAndItsValue()));
    }
}