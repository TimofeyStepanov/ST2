package ru.stepanoff.dto.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ru.stepanoff.dto.ProducerRuleDTO;

public class ProducerRuleDTOSerializer implements Serializer<ProducerRuleDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, ProducerRuleDTO producerRuleDTO) {
        try {
            return objectMapper.writeValueAsBytes(producerRuleDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
