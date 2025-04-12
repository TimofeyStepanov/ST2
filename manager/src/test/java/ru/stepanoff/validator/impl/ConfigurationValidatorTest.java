package ru.stepanoff.validator.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.stepanoff.constants.FieldType;
import ru.stepanoff.dto.*;
import ru.stepanoff.validator.ConfigurationValidator;
import ru.stepanoff.validator.exception.ConfigurationValidationException;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ConfigurationValidatorTest {

    @InjectMocks
    private ConfigurationValidator configurationValidator;

    /**
     * Проверка того, что выбросит ошибку, если не указана бд для топика
     */
    @Test
    void testEmptyTableName() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO(); // тут не указана бд
        inputTopicDTO1.setAlias("alias1");
        inputTopicDTO1.setTopicNames(Set.of("topic1, topic2"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO( "id_device", FieldType.STRING, false, true),
                new TopicFieldDTO( "event_time", FieldType.LONG, false, false)
        ));
        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("alias2");
        inputTopicDTO2.setTableName("alias2");
        inputTopicDTO2.setTopicNames(Set.of("topic3, topic4"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, false),
                new TopicFieldDTO("id_session", FieldType.STRING, false, false)
        ));

        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("topic5"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setTriggerCondition("alias1.id_device = alias2.id_device");
        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputBootstrapServers("kafka:9090")
                        .inputTopics(Set.of(inputTopicDTO1, inputTopicDTO2))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputBootstrapServers("kafka:9090")
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );

        Exception e = Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> configurationValidator.validateConfigurationAndThrowExceptionIfIncorrect(configurationDTO)
        );
        Assertions.assertEquals("Не указано имя БД для топика под псевдонимом: alias1", e.getMessage());
    }

    /**
     * Проверка того, что выбросит ошибку, если в условиях создания триггера указано неизвестное поле
     */
    @Test
    void testUnknownFieldsInTriggerCondition() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO();
        inputTopicDTO1.setAlias("alias1");
        inputTopicDTO1.setTableName("alias1");
        inputTopicDTO1.setTopicNames(Set.of("topic1, topic2"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO( "id_device", FieldType.STRING, false, true),
                new TopicFieldDTO( "event_time", FieldType.LONG, false, false)
        ));
        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("alias2");
        inputTopicDTO2.setTableName("alias2");
        inputTopicDTO2.setTopicNames(Set.of("topic3, topic4"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, false),
                new TopicFieldDTO("id_session", FieldType.STRING, false, false)
        ));

        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("topic5"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setTriggerCondition("alias1.id_device = unknown.field"); // неизвестное поле

        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputBootstrapServers("kafka:9090")
                        .inputTopics(Set.of(inputTopicDTO1, inputTopicDTO2))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputBootstrapServers("kafka:9090")
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );

        Exception e = Assertions.assertThrows(
                ConfigurationValidationException.class,
                () -> configurationValidator.validateConfigurationAndThrowExceptionIfIncorrect(configurationDTO)
        );
        Assertions.assertEquals("Неизвестное поле в условии для создания триггера: unknown.field", e.getMessage());
    }

    /**
     * Проверка того, что если конфигурация правильная, то никакую ошибку не выбросит
     */
    @Test
    void testCorrectConfiguration() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO();
        inputTopicDTO1.setAlias("alias1");
        inputTopicDTO1.setTableName("alias1");
        inputTopicDTO1.setTopicNames(Set.of("topic1, topic2"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO( "id_device", FieldType.STRING, false, true),
                new TopicFieldDTO( "event_time", FieldType.LONG, false, false)
        ));
        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("alias2");
        inputTopicDTO2.setTableName("alias2");
        inputTopicDTO2.setTopicNames(Set.of("topic3, topic4"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, false),
                new TopicFieldDTO("id_session", FieldType.STRING, false, false)
        ));
        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("topic5"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputBootstrapServers("kafka:9090")
                        .inputTopics(Set.of(inputTopicDTO1, inputTopicDTO2))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputBootstrapServers("kafka:9090")
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );
        configurationDTO.setTriggerCondition("alias1.id_device == alias2.id_device and alias1.id_device == alias2.id_session");


        Assertions.assertDoesNotThrow(() -> {
            configurationValidator.validateConfigurationAndThrowExceptionIfIncorrect(configurationDTO);
            return null;
        });
    }
}