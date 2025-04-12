package ru.stepanoff.dto.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ru.stepanoff.dto.ConsumerRuleDTO;

public class ConsumerRuleDTODeserializer implements Deserializer<ConsumerRuleDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConsumerRuleDTO deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, ConsumerRuleDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
