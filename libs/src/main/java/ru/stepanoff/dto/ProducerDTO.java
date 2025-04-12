package ru.stepanoff.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProducerDTO {
    private List<String> messages;
}
