package ru.stepanoff.validator;

import org.springframework.stereotype.Component;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.InputTopicDTO;
import ru.stepanoff.dto.TopicFieldDTO;
import ru.stepanoff.validator.exception.ConfigurationValidationException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.stepanoff.constants.FieldType.getAllTypeNames;
import static ru.stepanoff.constants.TriggerConditionDelimiter.DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD;
import static ru.stepanoff.constants.TriggerConditionDelimiter.getAllDelimiters;
import static ru.stepanoff.constants.TriggerConditionOperator.getAllOperatorNames;
import static ru.stepanoff.constants.TriggerConditionSign.getAllSignNames;


@Component
public class ConfigurationValidator {

    public void validateConfigurationAndThrowExceptionIfIncorrect(ConfigurationDTO configurationDTO) throws ConfigurationValidationException {
        Set<InputTopicDTO> inputTopics = configurationDTO.getInputKafkaInfo().getInputTopics();
        String triggerCondition = configurationDTO.getTriggerCondition();

        throwExceptionIfThereIsNoOneValueToReturn(inputTopics);
        throwExceptionIfNumberOfTopicsIsLessThanTwo(inputTopics);
        throwExceptionIfAliesAreNotUnique(inputTopics);
        throwExceptionIfTableNamesAreNotUnique(inputTopics);
//        throwExceptionIfThereAreUnknownFieldTypes(inputTopics);
        throwExceptionIfTableNamesInInputTopicsAreEmpty(inputTopics);
        throwExceptionIfInputTopicContainsSpecialSymbols(inputTopics);
//        throwExceptionIfInputTopicDoesNotContainValuesToReturn(inputTopics, valuesToReturn);
        throwExceptionIfTriggerConditionContainsUnknownSymbols(inputTopics, triggerCondition);
//        throwExceptionIfAliasFromValuesToReturnIsNotInTriggerCondition(valuesToReturn, triggerCondition);
    }

    /**
     * Проверка того, что есть хотя бы 1 значение для возврата
     */
    private void throwExceptionIfThereIsNoOneValueToReturn(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        long numberOfValuesToReturn = inputTopics.stream()
                .flatMap(inputTopic -> inputTopic.getTopicFields().stream())
                .filter(TopicFieldDTO::getNeedToReturn)
                .count();
        if (numberOfValuesToReturn > 0) {
            return;
        }
        throw new ConfigurationValidationException("Число полей для возврата должно быть > 0");
    }

    /**
     * Проверка того, что число топиков > 1
     */
    private void throwExceptionIfNumberOfTopicsIsLessThanTwo(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        if (inputTopics.size() < 2) {
            throw new ConfigurationValidationException("Число топиков для чтения должно быть > 1");
        }
    }

    /**
     * Проверка, что все псевдонимы уникальны
     */
    private void throwExceptionIfAliesAreNotUnique(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        int allNumberOfTopics = inputTopics.size();
        long numberOfUniqueAlies = inputTopics
                .stream()
                .map(InputTopicDTO::getAlias)
                .distinct()
                .count();
        if (allNumberOfTopics == numberOfUniqueAlies) {
            return;
        }
        throw new ConfigurationValidationException("Не все псевдонимы топиков уникальны");
    }

    /**
     * Проверка, что все имена таблиц уникальны
     */
    private void throwExceptionIfTableNamesAreNotUnique(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        int allNumberOfTopics = inputTopics.size();
        long numberOfUniqueTables = inputTopics
                .stream()
                .map(InputTopicDTO::getTableName)
                .distinct()
                .count();
        if (allNumberOfTopics == numberOfUniqueTables) {
            return;
        }
        throw new ConfigurationValidationException("Не все псевдонимы таблиц уникальны");
    }

//    /**
//     * Проверка, что все поля имеют известные типы
//     */
//    private void throwExceptionIfThereAreUnknownFieldTypes(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
//        Optional<String> unknownFieldType = inputTopics
//                .stream()
//                .flatMap(inputTopicDTO -> inputTopicDTO.getTopicFields().stream())
//                .map(TopicFieldDTO::getFieldType)
//                .map(String::toLowerCase)
//                .map(String::trim)
//                .filter(fieldType -> !getAllTypeNames().contains(fieldType))
//                .findFirst();
//        if (unknownFieldType.isEmpty()) {
//            return;
//        }
//        throw new ConfigurationValidationException("Неизвестный тип поля: " + unknownFieldType.get());
//    }

    /**
     * Проверка, если для топика не была указана связанная с ним бд
     */
    private void throwExceptionIfTableNamesInInputTopicsAreEmpty(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        for (InputTopicDTO inputTopic : inputTopics) {
            String tableName = inputTopic.getTableName();
            if (Objects.isNull(tableName) || tableName.isBlank()) {
                throw new ConfigurationValidationException("Не указано имя БД для топика под псевдонимом: " + inputTopic.getAlias());
            }
        }
    }

    /**
     * Проверка того, что поля не содержат специальные символы
     */
    private void throwExceptionIfInputTopicContainsSpecialSymbols(Set<InputTopicDTO> inputTopics) throws ConfigurationValidationException {
        Set<String> allDelimiters = getAllDelimiters();
        for (InputTopicDTO inputTopicDTO : inputTopics) {
            for (String delimiter : allDelimiters) {
                if (inputTopicDTO.getAlias().contains(delimiter) || inputTopicDTO.getTableName().contains(delimiter)) {
                    throw new ConfigurationValidationException("Псевдоним и таблица не могут содержать символ:" + delimiter);
                }

                Set<TopicFieldDTO> topicFields = inputTopicDTO.getTopicFields();
                for (TopicFieldDTO topicField : topicFields) {
                    if (topicField.getFieldName().contains(delimiter)) {
                        throw new ConfigurationValidationException("Поля топика не могут содержать символ:" + delimiter);
                    }
                }
            }
        }
    }

    /**
     * Проверка, все ли значения для возврата содержатся в топиках
     */
    private void throwExceptionIfInputTopicDoesNotContainValuesToReturn(Set<InputTopicDTO> inputTopics, List<String> valuesToReturn) throws ConfigurationValidationException {
        Set<String> allConcatenatedAliesWithField = getConcatenatedAliesWithField(inputTopics);

        Optional<String> unknownValueToReturn = valuesToReturn.stream()
                .filter(valueToReturn -> !allConcatenatedAliesWithField.contains(valueToReturn))
                .findFirst();
        if (unknownValueToReturn.isEmpty()) {
            return;
        }
        throw new ConfigurationValidationException("Неизвестное поле для возврата: " + unknownValueToReturn.get());
    }

    /**
     * Проверка, что условие для создания триггера состоит только из знаков и полей топиков.
     */
    private void throwExceptionIfTriggerConditionContainsUnknownSymbols(Set<InputTopicDTO> inputTopics, String triggerCondition) throws ConfigurationValidationException {
        Set<String> allConcatenatedAliesWithField = getConcatenatedAliesWithField(inputTopics);

        String conditionForOperatorSplit = "(" + String.join("|", getAllOperatorNames()) + ")";
        String conditionForSignSplit = "(" + String.join("|", getAllSignNames()) + ")";
        Optional<String> unknownConcatenatedAliesWithFieldFromTriggerCondition = Arrays
                .stream(triggerCondition.split(conditionForOperatorSplit))
                .filter(twoConcatenatedAliasWithFieldAndSign -> !twoConcatenatedAliasWithFieldAndSign.isBlank())
                .map(String::trim)
                .flatMap(twoConcatenatedAliasWithFieldWithSign -> Arrays.stream(twoConcatenatedAliasWithFieldWithSign.split(conditionForSignSplit)))
                .map(String::trim)
                .filter(concatenatedAliesWithField -> !concatenatedAliesWithField.isBlank())
                .filter(concatenatedAliesWithField -> !allConcatenatedAliesWithField.contains(concatenatedAliesWithField))
                .findFirst();
        if (unknownConcatenatedAliesWithFieldFromTriggerCondition.isEmpty()) {
            return;
        }
        throw new ConfigurationValidationException("Неизвестное поле в условии для создания триггера: " + unknownConcatenatedAliesWithFieldFromTriggerCondition.get());
    }

    /**
     * Проврерка того, что все топики в значениях для возрата были в условии для создания триггера
     */
    private void throwExceptionIfAliasFromValuesToReturnIsNotInTriggerCondition(List<String> valuesToReturn, String triggerCondition) throws ConfigurationValidationException {
        String conditionForOperatorSplit = "(" + String.join("|", getAllOperatorNames()) + ")";
        String conditionForSignSplit = "(" + String.join("|", getAllSignNames()) + ")";

        Set<String> allAliasesFromTriggerCondition = Arrays
                .stream(triggerCondition.split(conditionForOperatorSplit))
                .filter(twoConcatenatedAliasWithFieldAndSign -> !twoConcatenatedAliasWithFieldAndSign.isBlank())
                .map(String::trim)
                .flatMap(twoConcatenatedAliasWithFieldWithSign -> Arrays.stream(twoConcatenatedAliasWithFieldWithSign.split(conditionForSignSplit)))
                .map(String::trim)
                .filter(concatenatedAliesWithField -> !concatenatedAliesWithField.isBlank())
                .map(concatenatedAliesWithField -> {
                    int indexOfDelimiter = concatenatedAliesWithField.indexOf(DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue);
                    return concatenatedAliesWithField.substring(0, indexOfDelimiter).trim();
                })
                .collect(Collectors.toSet());

        Optional<String> aliasThatIsInValuesToReturnButNotInTriggerCondition = valuesToReturn
                .stream()
                .map(valueToReturn -> {
                    int indexOfDelimiter = valueToReturn.indexOf(DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue);
                    return valueToReturn.substring(0, indexOfDelimiter).trim();
                })
                .map(String::trim)
                .filter(aliasFromValueToReturn -> !allAliasesFromTriggerCondition.contains(aliasFromValueToReturn))
                .findFirst();
        if (aliasThatIsInValuesToReturnButNotInTriggerCondition.isEmpty()) {
            return;
        }
        throw new ConfigurationValidationException((
                "Топик под псевдонимом '%s' есть в полях для возврата, но его нет в условии для создания триггера").formatted(aliasThatIsInValuesToReturnButNotInTriggerCondition.get())
        );
    }


    private Set<String> getConcatenatedAliesWithField(Set<InputTopicDTO> inputTopics) {
        return inputTopics.stream()
                .flatMap(inputTopic -> inputTopic.getTopicFields()
                        .stream()
                        .map(topicField -> inputTopic.getAlias() + DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue + topicField.getFieldName())
                )
                .collect(Collectors.toSet());
    }
}
