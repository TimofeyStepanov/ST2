package ru.stepanoff.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.stepanoff.client.KafkaClient;
import ru.stepanoff.client.MetricsClient;
import ru.stepanoff.client.impl.UserKafkaKafkaClient;
import ru.stepanoff.constants.MessagesEnum;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.service.ProducerService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final UserKafkaKafkaClient userClient;
    private final KafkaClient managerKafkaKafkaClient;
    private final MetricsClient metricsClient;

    private Optional<ProducerRuleDTO> optionalCurrentProducerRule = Optional.empty();

    @EventListener({ContextRefreshedEvent.class})
    void sendMessageToManagerToGetRule() {
        managerKafkaKafkaClient.sendAsync(MessagesEnum.RULE_REPEAT.message);
    }

    @Override
    public void sendToUser(List<String> messages) {
        messages.forEach(message -> {
            userClient.sendAsync(message);
            metricsClient.incrementNumberOfSentMessagesToUser();
        });
    }

    @Override
    public void updateProducerRule(ProducerRuleDTO newProducerRule) {
        if (!needToUpdateRule(newProducerRule)) {
            return;
        }

        log.debug("Новое правило: {}", newProducerRule);
        optionalCurrentProducerRule = Optional.of(newProducerRule);

        userClient.updateConfig(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, newProducerRule.getKafkaBootstrapServices(),
                        ProducerConfig.CLIENT_ID_CONFIG, newProducerRule.getClientId(),

                        ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, newProducerRule.getRequestTimeoutMs(),
                        ProducerConfig.BATCH_SIZE_CONFIG, newProducerRule.getBatchSize(),
                        ProducerConfig.BUFFER_MEMORY_CONFIG, newProducerRule.getBufferMemory(),
                        ProducerConfig.LINGER_MS_CONFIG, newProducerRule.getLingerMs(),
                        ProducerConfig.ACKS_CONFIG, newProducerRule.getAcks()
                ),
                newProducerRule.getTopicsToWrite()
        );
    }

    private boolean needToUpdateRule(ProducerRuleDTO newProducerRule) {
        if (optionalCurrentProducerRule.isEmpty()) {
            return true;
        }

        ProducerRuleDTO currentProducerRule = optionalCurrentProducerRule.get();
        if (!newProducerRule.equals(currentProducerRule)) {
            return false;
        }
        return newProducerRule.getCreatedAt() > currentProducerRule.getCreatedAt();
    }
}
