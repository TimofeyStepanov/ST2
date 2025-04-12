package ru.stepanoff.document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "producer")
public class ProducerRule {
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
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * CLIENT_ID_CONFIG для Кафки
     */
    private String clientId;

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
}
