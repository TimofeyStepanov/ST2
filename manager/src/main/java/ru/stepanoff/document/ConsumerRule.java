package ru.stepanoff.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consumer")
public class ConsumerRule {
    /**
     * Информация о полях из входящего сообщения Кафки для одного топика
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfo {
        /**
         * Поля, которые надо вытащить из сообщения от конкретного топика
         */
        private Set<String> fieldsToTake;

        /**
         * Названия топиков, из которых будут поступать данные. Сделано множеством для масштабирования Кафки
         */
        private Set<String> topicNames;
    }
    /**
     * Адрес Кафки для чтения данных от пользователя
     */
    private String kafkaBootstrapServices;

    /**
     * AUTO_OFFSET_RESET_CONFIG для Кафки
     */
    private String kafkaAutoOffsetReset;

    /**
     * REQUEST_TIMEOUT_MS_CONFIG для Кафки
     */
    private Integer requestTimeoutMs;

    /**
     * GROUP_ID_CONFIG для Кафки
     */
    private String groupId;

    /**
     * Время создания сущности
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * Словарь, где ключ - псевдоним топика, значение - информация для конректного топика
     */
    private Map<String, ConsumerRule.TopicInfo> topicAliasAndItsInfo;
}
