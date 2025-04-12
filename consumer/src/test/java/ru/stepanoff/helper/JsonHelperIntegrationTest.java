package ru.stepanoff.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.stepanoff.helper.exception.JsonHelperException;

import java.util.Map;
import java.util.Set;

@SpringBootTest
class JsonHelperIntegrationTest {

    @Autowired
    private JsonHelper jsonHelper;

    @Test
    void testCorrectJson() throws JsonHelperException {
        String jsonString = "{\"name\":\"Иван\",\"age\":30}";
        Map<String, String> actual = jsonHelper.getFieldsWithValuesFromJson(jsonString, Set.of("name"));
        Map<String, String> expected = Map.of(
                "name", "'Иван'"
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testUnknownFieldToTakeFromJson() {
        String jsonString = "{\"name\":\"Иван\",\"age\":30}";
        Exception e = Assertions.assertThrows(
                JsonHelperException.class,
                () -> jsonHelper.getFieldsWithValuesFromJson(jsonString, Set.of("unknow_field"))
        );
        Assertions.assertEquals(
                "Нет поля '%s' в объекте '%s'".formatted("unknow_field", jsonString),
                e.getMessage())
        ;
    }

    @Test
    void testUnknownTypeInJson() {
        String jsonString = "{\"name\":\"Иван\",\"age\":[30, 12]}";
        Exception e = Assertions.assertThrows(
                JsonHelperException.class,
                () -> jsonHelper.getFieldsWithValuesFromJson(jsonString, Set.of("age"))
        );
        Assertions.assertEquals(
                "Объект '%s' содержит недоступный тип".formatted("[30, 12]"),
                e.getMessage())
        ;
    }
}