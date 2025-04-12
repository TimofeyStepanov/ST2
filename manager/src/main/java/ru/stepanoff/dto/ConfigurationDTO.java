package ru.stepanoff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Настройка правил работы всей системы
 */
@Data
public class ConfigurationDTO {
    /**
     * Информация о Кафке, необходимая для чтения данных (для consumer)
     */
    private InputKafkaInfo inputKafkaInfo;
    /**
     * Информация о Кафке, необходимая для записи данных (для producer)
     */
    private OutputKafkaInfo outputKafkaInfo;
    /**
     * Условие для создания триггера
     */
    @NotBlank(message = "Нет информации об условии для создания триггера")
    private String triggerCondition;
}
