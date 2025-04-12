package ru.stepanoff.dto;

import lombok.*;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorRuleDTO {
    /**
     * Информация о запросах insert/select для одного топика
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicInfo {
        /**
         * DML запрос на вставку данных в processor
         */
        private String insertRequest;

        /**
         * DML запрос на получение данных в processor
         */
        private String selectRequest;
        /**
         * Имя таблицы БД топика
         */
        private String tableName;
    }

    /**
     * DDL запросы для processor
     */
    private List<String> scriptsDDL;

    /**
     * Словарь, где ключ - псевдоним топика, значение - информация для конректного топика
     */
    private Map<String, ProcessorRuleDTO.TopicInfo> topicAliasAndItsInfo;

    /**
     * Время создания сущности
     */
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private long createdAt = System.currentTimeMillis();
}
