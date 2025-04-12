package ru.stepanoff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stepanoff.constants.FieldType;

/**
 * Информация о поле топика Кафки
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicFieldDTO {
    /**
     * Название поля
     */
    @NotBlank(message = "Название поля топика не может быть пустым")
    private String fieldName;
    /**
     * Тип поля
     */
    @NotBlank(message = "Тип поля топика не может быть пустым")
    private FieldType fieldType;
    /**
     * Надо ли ставить индекс для этого поля
     */
    private Boolean isIndexed = false;
    /**
     * Надо ли вернуть при условии для создания триггера
     */
    private Boolean needToReturn = false;
}
