package ru.stepanoff.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessorDTO {
    private String topicAlias;
    private Map<String, String> fieldNameAndItsValue;
}
