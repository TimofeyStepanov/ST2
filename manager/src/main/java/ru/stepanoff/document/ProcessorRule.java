package ru.stepanoff.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "processor")
public class ProcessorRule {
    /**
     * Информация о запросах insert/select для одного топика
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    private List<String> ddlScripts;

    /**
     * Время создания сущности
     */
    @Builder.Default
    private long createdAt = System.currentTimeMillis();

    /**
     * Словарь, где ключ - псевдоним пропущенного значения в запросах (insert и select), который надо заменить на значение - поле из топика
     */
    private Map<String, String> aliasInRequestAndItsTopicField;

    /**
     * Словарь, где ключ - псевдоним топика, значение - информация для конректного топика
     */
    private Map<String, TopicInfo> topicAliasAndItsInfo;
}
