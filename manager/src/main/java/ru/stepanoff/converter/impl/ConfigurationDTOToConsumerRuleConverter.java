package ru.stepanoff.converter.impl;

import org.springframework.stereotype.Component;
import ru.stepanoff.converter.Converter;
import ru.stepanoff.dto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfigurationDTOToConsumerRuleConverter implements Converter<ConfigurationDTO, ConsumerRuleDTO> {

    @Override
    public ConsumerRuleDTO convert(ConfigurationDTO configuration) {
        ConsumerRuleDTO consumerRule = new ConsumerRuleDTO();
        consumerRule.setKafkaBootstrapServices(configuration.getInputKafkaInfo().getInputBootstrapServers());
        consumerRule.setGroupId(configuration.getInputKafkaInfo().getGroupId());
        consumerRule.setRequestTimeoutMs(configuration.getInputKafkaInfo().getRequestTimeoutMs());
        consumerRule.setKafkaAutoOffsetReset(configuration.getInputKafkaInfo().getKafkaAutoOffsetReset());

        Map<String, ConsumerRuleDTO.TopicInfo> topicAliasAndItsInfo = new HashMap<>();
        for (InputTopicDTO inputTopic : configuration.getInputKafkaInfo().getInputTopics()) {
            topicAliasAndItsInfo.put(
                    inputTopic.getAlias(),
                    new ConsumerRuleDTO.TopicInfo(
                            inputTopic.getTopicFields().stream().map(TopicFieldDTO::getFieldName).collect(Collectors.toSet()),
                            inputTopic.getTopicNames()
                    )
            );
        }
        consumerRule.setTopicAliasAndItsInfo(topicAliasAndItsInfo);
        consumerRule.setReadyToStartConsuming(true);
        return consumerRule;
    }
}
