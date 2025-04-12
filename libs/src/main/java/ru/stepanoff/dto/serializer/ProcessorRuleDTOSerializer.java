package ru.stepanoff.dto.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ru.stepanoff.dto.ProcessorRuleDTO;

public class ProcessorRuleDTOSerializer implements Serializer<ProcessorRuleDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, ProcessorRuleDTO processorRuleDTO) {
        try {
            return objectMapper.writeValueAsBytes(processorRuleDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
