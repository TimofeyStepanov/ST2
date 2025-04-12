package ru.stepanoff.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

/**
 * Информация о топике Кафки, необходимая для записи данных (для producer)
 */
@Data
public class OutputTopicDTO {
    /**
     * Действительные имена топиков в Кафке (сделано списком для масштабируемости Кафки)
     */
    @NotEmpty(message = "Укажите хотя бы один топик")
    private Set<String> topicNames;
}
