package ru.stepanoff.dto.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerRuleDTO;

public class ProducerRuleDTODeserializer implements Deserializer<ProducerRuleDTO> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProducerRuleDTO deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, ProducerRuleDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
