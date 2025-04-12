package ru.stepanoff.dto.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ru.stepanoff.dto.ProcessorRuleDTO;

public class ProcessorRuleDTODeserializer implements Deserializer<ProcessorRuleDTO> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProcessorRuleDTO deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, ProcessorRuleDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
