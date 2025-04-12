package ru.stepanoff.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducerRuleDTO {
    /**
     * Адрес Кафки для записи данных, полученных от processor
     */
    private String kafkaBootstrapServices;

    /**
     * Топки Кафки для записи данных, полученных от processor. Сделано множеством для масштабирования Кафки
     */
    private Set<String> topicsToWrite;
    /**
     * Время создания сущности
     */
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * BATCH_SIZE_CONFIG для Кафки
     */
    private Integer batchSize;

    /**
     * REQUEST_TIMEOUT_MS_CONFIG для Кафки
     */

    private Integer requestTimeoutMs;

    /**
     * BUFFER_MEMORY_CONFIG для Кафки
     */
    private Integer bufferMemory;

    /**
     * LINGER_MS_CONFIG для Кафки
     */
    private Integer lingerMs;

    /**
     * ACKS_CONFIG для Кафки
     */
    private String acks;

    /**
     * CLIENT_ID_CONFIG для Кафки
     */
    private String clientId;
}
