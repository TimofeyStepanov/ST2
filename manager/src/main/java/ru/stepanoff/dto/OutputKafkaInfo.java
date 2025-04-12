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
public class OutputKafkaInfo {
    /**
     * Информация о топиках Кафки, необходимая для записи данных (для producer)
     */
    @NotEmpty(message = "Нет информации о топиках для записи")
    private Set<OutputTopicDTO> outputTopic;

    /**
     * Адрес Кафки для записи данных
     */
    @NotBlank(message = "Адрес Кафки не может быть пустым")
    private String outputBootstrapServers;

    /**
     * CLIENT_ID_CONFIG для Кафки
     */
    @Builder.Default
    private String clientId = UUID.randomUUID().toString();

    /**
     * BATCH_SIZE_CONFIG для Кафки
     */
    @Builder.Default
    @Min(value = 1, message = "BatchSize должен быть > 0")
    private Integer batchSize = 16384;

    /**
     * REQUEST_TIMEOUT_MS_CONFIG для Кафки
     */
    @Builder.Default
    @Min(value = 1, message = "RequestTimoutMs должен быть > 0")
    private Integer requestTimeoutMs = 30000;

    /**
     * BUFFER_MEMORY_CONFIG для Кафки
     */
    @Builder.Default
    @Min(value = 1, message = "BufferMemory должен быть > 0")
    private Integer bufferMemory = 33554432;

    /**
     * LINGER_MS_CONFIG для Кафки
     */
    @Builder.Default
    @Min(value = 1, message = "LingerMs должен быть > 0")
    private Integer lingerMs = 30000;

    /**
     * ACKS_CONFIG для Кафки
     */
    @Builder.Default
    @Pattern(regexp = "^(-1|0|1)$", message = "Acks должен быть значением из {-1, 0, 1}")
    private String acks = "1";
}
