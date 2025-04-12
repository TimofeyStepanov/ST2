package ru.stepanoff.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputKafkaInfo {
    /**
     * Информация о топиках Кафки, необходимая для чтения данных (для consumer)
     */
    @NotEmpty(message = "Нет информации о топиках для чтения")
    private Set<InputTopicDTO> inputTopics;

    /**
     * Адрес Кафки для чтения данных
     */
    @NotBlank(message = "Адрес Кафки не может быть пустым")
    private String inputBootstrapServers;

    /**
     * AUTO_OFFSET_RESET_CONFIG для Кафки
     */
    @Builder.Default
    private String groupId = UUID.randomUUID().toString();

    /**
     * AUTO_OFFSET_RESET_CONFIG для Кафки
     */
    @Builder.Default
    @Pattern(regexp = "^(latest|earliest)$", message = "Неверный kafkaAutoOffsetReset")
    private String kafkaAutoOffsetReset = "latest";

    /**
     * REQUEST_TIMEOUT_MS_CONFIG для Кафки
     */
    @Builder.Default
    @Min(value = 1, message = "RequestTimeoutMs должен быть > 0")
    private Integer requestTimeoutMs = 30000;
}
