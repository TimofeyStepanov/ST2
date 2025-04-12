package ru.stepanoff.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.stepanoff.helper.exception.JsonHelperException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper mapper;

    /**
     * @param json - строка в формате json
     * @param fieldsToTakeFromJson - поля, которые надо вытащить из строки json
     * @return словарь, где ключи - строки из fieldsToTakeFromJson, а значения словаря - взятое из строки json значения для ключа
     */
    public Map<String, String> getFieldsWithValuesFromJson(String json, Set<String> fieldsToTakeFromJson) throws JsonHelperException {
        try {
            Map<String, String> fieldsAndItsValues = new HashMap<>();

            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<>() {});
            for (String fieldToTakeFromJson : fieldsToTakeFromJson) {
                if (!jsonMap.containsKey(fieldToTakeFromJson)) {
                    throw new JsonHelperException("Нет поля '%s' в объекте '%s'".formatted(fieldToTakeFromJson, json));
                }
                fieldsAndItsValues.put(fieldToTakeFromJson, convertObjectIntoJsonString(jsonMap.get(fieldToTakeFromJson)));
            }
            return fieldsAndItsValues;
        } catch (JsonProcessingException e) {
            throw new JsonHelperException(e);
        }
    }

    private String convertObjectIntoJsonString(Object object) throws JsonHelperException {
        if (object instanceof Integer || object instanceof Double ||
            object instanceof Float || object instanceof Short ||
            object instanceof Long) {
            return object.toString();
        }
        if (object instanceof String) {
            return "\"" + object + "\"";
        }
        throw new JsonHelperException("Объект '%s' содержит недоступный тип".formatted(object.toString()));
    }
}
