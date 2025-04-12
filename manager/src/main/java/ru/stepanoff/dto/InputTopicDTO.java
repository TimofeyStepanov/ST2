package ru.stepanoff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


/**
 * Информация о топике Кафки, необходимая для чтения данных (для consumer)
 */
@Data
public class InputTopicDTO {
    /**
     * Единый псевдоним для топиков
     */
    @NotBlank(message = "Нет псведонима для топиков")
    private String alias;

    /**
     * Действительные имена топиков в Кафке (сделано списком для масштабируемости Кафки)
     */
    @NotEmpty(message = "Укажите хотя бы один топик")
    private Set<String> topicNames;

    /**
     * Информация о полях топика
     */
    @NotEmpty(message = "Укажите хотя бы одно поле")
    private Set<TopicFieldDTO> topicFields;

    /**
     * Имя для таблицы топика в БД. Поле может быть пустым, по умолчанию будет равно alias'у.
     */
    private String tableName;
}
