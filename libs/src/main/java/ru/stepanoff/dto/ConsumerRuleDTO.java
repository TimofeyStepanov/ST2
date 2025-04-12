package ru.stepanoff.dto;

import lombok.*;

import java.util.Map;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerRuleDTO {
    /**
     * Информация о полях из входящего сообщения Кафки для одного топика
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
     * GROUP_ID_CONFIG для Кафки
     */
    private String groupId;

    /**
     * AUTO_OFFSET_RESET_CONFIG для Кафки
     */
    private String kafkaAutoOffsetReset;

    /**
     * REQUEST_TIMEOUT_MS_CONFIG для Кафки
     */
    private Integer requestTimeoutMs;

    /**
     * Словарь, где ключ - псевдоним топика, значение - информация для конректного топика
     */
    private Map<String, ConsumerRuleDTO.TopicInfo> topicAliasAndItsInfo;

    /**
     * Если флаг выставлен в true, то нельзя начинать потреблять данные
     */
    @Builder.Default
    private boolean readyToStartConsuming = true;

    /**
     * Время создания сущности
     */
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * Число потоков для работы потребителя
     */
    @Builder.Default
    private int numberOfConsumerThreads = 3;
}
