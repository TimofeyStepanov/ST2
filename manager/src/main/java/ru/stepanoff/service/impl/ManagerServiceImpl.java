package ru.stepanoff.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.stepanoff.client.ConsumerClient;
import ru.stepanoff.client.ProcessorClient;
import ru.stepanoff.client.ProducerClient;
import ru.stepanoff.converter.Converter;
import ru.stepanoff.document.ConsumerRule;
import ru.stepanoff.document.ProcessorRule;
import ru.stepanoff.document.ProducerRule;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.repository.ConsumerRepository;
import ru.stepanoff.repository.ProcessorRepository;
import ru.stepanoff.repository.ProducerRepository;
import ru.stepanoff.service.ManagerService;
import ru.stepanoff.service.exception.ServiceUpdatingException;
import ru.stepanoff.validator.ConfigurationValidator;
import ru.stepanoff.validator.exception.ConfigurationValidationException;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ConfigurationValidator configurationValidator;

    private final Converter<ConfigurationDTO, ConsumerRuleDTO> configurationDTOToConsumerRuleConverter;
    private final Converter<ConfigurationDTO, ProcessorRuleDTO> configurationDTOToProcessorRuleConverter;
    private final Converter<ConfigurationDTO, ProducerRuleDTO> configurationDTOToProducerRuleConverter;

    private final ConsumerRepository consumerRepository;
    private final ProcessorRepository processorRepository;
    private final ProducerRepository producerRepository;

    private final ConsumerClient consumerClient;
    private final ProcessorClient processorClient;
    private final ProducerClient producerClient;

    private final ModelMapper mapper;

    @Override
    public void updateConfiguration(ConfigurationDTO configurationDTO)
            throws ServiceUpdatingException {
        setDefaultDBNamesIfItNecessary(configurationDTO);
        validateConfigurationAndThrowExceptionIfIncorrect(configurationDTO);
        saveConfiguration(configurationDTO);
        updateProducerWithTheLatestConfigurationIfItExists();
        updateProcessorWithTheLatestConfigurationIfItExists();
        updateConsumerWithTheLatestConfigurationIfItExists();
    }

    private void setDefaultDBNamesIfItNecessary(ConfigurationDTO configurationDTO) {
        configurationDTO
                .getInputKafkaInfo()
                .getInputTopics()
                .stream()
                .filter(inputTopicDTO -> isNull(inputTopicDTO.getTableName()) || inputTopicDTO.getTableName().isBlank())
                .forEach(inputTopicDTO -> inputTopicDTO.setTableName(inputTopicDTO.getAlias()));
    }

    private void validateConfigurationAndThrowExceptionIfIncorrect(ConfigurationDTO configurationDTO) throws ServiceUpdatingException {
        try {
            configurationValidator.validateConfigurationAndThrowExceptionIfIncorrect(configurationDTO);
        } catch (ConfigurationValidationException e) {
            throw new ServiceUpdatingException(e);
        }
    }

    private void saveConfiguration(ConfigurationDTO configurationDTO) {
        ConsumerRuleDTO consumerRuleDTO = configurationDTOToConsumerRuleConverter.convert(configurationDTO);
        ProcessorRuleDTO processorRuleDTO = configurationDTOToProcessorRuleConverter.convert(configurationDTO);
        ProducerRuleDTO producerRuleDTO = configurationDTOToProducerRuleConverter.convert(configurationDTO);

        consumerRepository.save(mapper.map(consumerRuleDTO, ConsumerRule.class));
        processorRepository.save(mapper.map(processorRuleDTO, ProcessorRule.class));
        producerRepository.save(mapper.map(producerRuleDTO, ProducerRule.class));
    }

    @Override
    public void updateConsumerWithTheLatestConfigurationIfItExists() {
        Optional<ConsumerRule> optionalConsumerRule = consumerRepository.findTopByOrderByCreatedAtDesc();
        if (optionalConsumerRule.isEmpty()) {
            return;
        }

        ConsumerRule consumerRule = optionalConsumerRule.get();
        consumerClient.sendRule(mapper.map(consumerRule, ConsumerRuleDTO.class));
    }

    @Override
    public void updateProcessorWithTheLatestConfigurationIfItExists() {
        Optional<ProcessorRule> optionalProcessorRule = processorRepository.findTopByOrderByCreatedAtDesc();
        if (optionalProcessorRule.isEmpty()) {
            return;
        }

        ProcessorRule processorRule = optionalProcessorRule.get();
        processorClient.sendRule(mapper.map(processorRule, ProcessorRuleDTO.class));
    }

    @Override
    public void updateProducerWithTheLatestConfigurationIfItExists() {
        Optional<ProducerRule> optionalProducerRule = producerRepository.findTopByOrderByCreatedAtDesc();
        if (optionalProducerRule.isEmpty()) {
            return;
        }

        ProducerRule producerRule = optionalProducerRule.get();
        producerClient.sendRule(mapper.map(producerRule, ProducerRuleDTO.class));
    }


    @Override
    public void stopConsumer() {
        ConsumerRuleDTO consumerRuleDTO = new ConsumerRuleDTO();
        consumerRuleDTO.setReadyToStartConsuming(true);
        consumerClient.sendRule(consumerRuleDTO);
    }

    @Override
    public Optional<ConsumerRuleDTO> getConsumerRule() {
        Optional<ConsumerRule> optionalConsumerRule = consumerRepository.findTopByOrderByCreatedAtDesc();
        if (optionalConsumerRule.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.map(optionalConsumerRule.get(), ConsumerRuleDTO.class));
    }

    @Override
    public Optional<ProcessorRuleDTO> getProcessorRule() {
        Optional<ProcessorRule> optionalProcessorRule = processorRepository.findTopByOrderByCreatedAtDesc();
        if (optionalProcessorRule.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.map(optionalProcessorRule.get(), ProcessorRuleDTO.class));
    }

    @Override
    public Optional<ProducerRuleDTO> getProducerRule() {
        Optional<ProducerRule> optionalProducerRule = producerRepository.findTopByOrderByCreatedAtDesc();
        if (optionalProducerRule.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.map(optionalProducerRule.get(), ProducerRuleDTO.class));
    }
}
