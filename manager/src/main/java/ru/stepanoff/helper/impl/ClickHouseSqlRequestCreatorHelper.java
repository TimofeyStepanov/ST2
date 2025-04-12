package ru.stepanoff.helper.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.stepanoff.constants.FieldType;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.InputTopicDTO;
import ru.stepanoff.dto.TopicFieldDTO;

import java.util.*;
import java.util.stream.Collectors;

import static ru.stepanoff.constants.FieldType.*;

@Component
public class ClickHouseSqlRequestCreatorHelper extends AbstractSqlRequestCreatorHelper {

    private static final Map<FieldType, String> topicFieldTypeAndItsTypeInClickHouse = Map.of(
            STRING, "String",
            INT, "Int",
            LONG, "Int256",
            DOUBLE, "Float648"
    );

    protected ClickHouseSqlRequestCreatorHelper(@Value("${processor.db.dbName}")String dbName) {
        super(dbName);
    }


    @Override
    public Map<String, String> createSelectRequestsForEachTopicInConfiguration(ConfigurationDTO configurationDTO) {
        Map<String, String> topicAndItsSelectRequest = super.createSelectRequestsForEachTopicInConfiguration(configurationDTO);
        topicAndItsSelectRequest.keySet()
                .forEach(topicName -> {
                    String selectRequest = topicAndItsSelectRequest.get(topicName);
                    topicAndItsSelectRequest.put(topicName, selectRequest + " format JSONEachRow");
                });
        return topicAndItsSelectRequest;
    }

    @Override
    public List<String> createDDLRequests(ConfigurationDTO configurationDTO) {
        List<String> ddlRequests = new ArrayList<>();
        ddlRequests.add("CREATE DATABASE IF NOT EXISTS %s ON CLUSTER '{cluster}'".formatted(dbName));

        configurationDTO.getInputKafkaInfo()
                .getInputTopics()
                .forEach(inputTopic -> {
                    String tableName = inputTopic.getTableName();

                    StringBuilder tableCreationRequest = new StringBuilder();
                    tableCreationRequest.append("CREATE TABLE IF NOT EXISTS %s.%s ON CLUSTER '{cluster}'"
                            .formatted(dbName, inputTopic.getTableName()));

                    tableCreationRequest.append(" (");
                    tableCreationRequest.append(
                            inputTopic.getTopicFields()
                                    .stream()
                                    .map(topicField -> topicField.getFieldName() + " " + topicFieldTypeAndItsTypeInClickHouse.get(topicField.getFieldType()))
                                    .collect(Collectors.joining(", "))
                    );
                    tableCreationRequest.append(") ");
                    tableCreationRequest.append(
                            "ENGINE = ReplicatedMergeTree('/clickhouse/{cluster}/%s/%s/{shard}','{replica}')".formatted(dbName, tableName)
                    );

                    if (inputTopic.getTopicFields().stream().anyMatch(TopicFieldDTO::getIsIndexed)) {
                        tableCreationRequest.append(" ORDER BY (");
                        tableCreationRequest.append(
                                inputTopic.getTopicFields()
                                        .stream()
                                        .filter(TopicFieldDTO::getIsIndexed)
                                        .map(TopicFieldDTO::getFieldName)
                                        .collect(Collectors.joining(", "))
                        );
                        tableCreationRequest.append(")");
                    }
                    ddlRequests.add(tableCreationRequest.toString());

                    ddlRequests.add(
                            "CREATE TABLE IF NOT EXISTS %s.%s_distributed ON CLUSTER '{cluster}' AS %s.%s ".formatted(dbName, tableName, dbName, tableName) +
                            "ENGINE = Distributed('{cluster}', %s, %s, rand())".formatted(dbName, tableName)
                    );
                });
        return ddlRequests;
    }

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - название топика (его алиас), а значение - запрос на вставку в БД для его данных
     */
    @Override
    public Map<String, String> createInsertRequestsForEachTopicInConfiguration(ConfigurationDTO configurationDTO) {
        Map<String, String> topicAndItsInsertRequest = new HashMap<>();
        for (InputTopicDTO inputTopicDTO : configurationDTO.getInputKafkaInfo().getInputTopics()) {
            Set<String> topicFieldsNames = inputTopicDTO.getTopicFields()
                    .stream()
                    .map(TopicFieldDTO::getFieldName)
                    .collect(Collectors.toSet());
            String insertRequest = "insert into %s.%s_distributed(%s) values(%s)".formatted(
                    dbName,
                    inputTopicDTO.getTableName(),
                    String.join(", ", topicFieldsNames),
                    String.join(", ", topicFieldsNames.stream().map(topicField -> "?").toList())
            );
            topicAndItsInsertRequest.put(inputTopicDTO.getAlias(), insertRequest);
        }
        return topicAndItsInsertRequest;
    }
}
