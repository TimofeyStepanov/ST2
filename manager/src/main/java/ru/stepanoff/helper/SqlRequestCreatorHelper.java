package ru.stepanoff.helper;

import ru.stepanoff.dto.ConfigurationDTO;

import java.util.List;
import java.util.Map;

public interface SqlRequestCreatorHelper {
    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - название топика (его алиас), а значение - запрос на вставку в БД для его данных
     */
    Map<String, String> createSelectRequestsForEachTopicInConfiguration(ConfigurationDTO configurationDTO);

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - название топика (его алиас), а значение - запрос на вставку в БД для его данных
     */
    Map<String, String> createInsertRequestsForEachTopicInConfiguration(ConfigurationDTO configurationDTO);

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return список DDL запрсов
     */
    List<String> createDDLRequests(ConfigurationDTO configurationDTO);
}
