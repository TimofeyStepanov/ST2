package ru.stepanoff.dto.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ru.stepanoff.dto.ConsumerRuleDTO;

public class ConsumerRuleDTOSerializer implements Serializer<ConsumerRuleDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, ConsumerRuleDTO consumerRuleDTO) {
        try {
            return objectMapper.writeValueAsBytes(consumerRuleDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
