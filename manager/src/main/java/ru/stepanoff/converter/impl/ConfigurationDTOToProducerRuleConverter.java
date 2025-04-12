package ru.stepanoff.converter.impl;

import org.springframework.stereotype.Component;
import ru.stepanoff.converter.Converter;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.OutputTopicDTO;
import ru.stepanoff.dto.ProducerRuleDTO;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ConfigurationDTOToProducerRuleConverter implements Converter<ConfigurationDTO, ProducerRuleDTO> {
    @Override
    public ProducerRuleDTO convert(ConfigurationDTO configuration) {
        ProducerRuleDTO producerRule = new ProducerRuleDTO();
        producerRule.setKafkaBootstrapServices(configuration.getOutputKafkaInfo().getOutputBootstrapServers());
        producerRule.setClientId(configuration.getOutputKafkaInfo().getClientId());
        producerRule.setRequestTimeoutMs(configuration.getOutputKafkaInfo().getRequestTimeoutMs());
        producerRule.setBatchSize(configuration.getOutputKafkaInfo().getBatchSize());
        producerRule.setBufferMemory(configuration.getOutputKafkaInfo().getBufferMemory());
        producerRule.setLingerMs(configuration.getOutputKafkaInfo().getLingerMs());
        producerRule.setAcks(configuration.getOutputKafkaInfo().getAcks());
        producerRule.setTopicsToWrite(
                configuration.getOutputKafkaInfo()
                        .getOutputTopic()
                        .stream()
                        .map(OutputTopicDTO::getTopicNames)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
        );
        return producerRule;
    }
}
