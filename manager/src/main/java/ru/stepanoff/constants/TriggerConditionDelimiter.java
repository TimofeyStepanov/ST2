package ru.stepanoff.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TriggerConditionDelimiter {
    DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD(".");

    public final String delimiterValue;

    TriggerConditionDelimiter(String delimiterValue) {
        this.delimiterValue = delimiterValue;
    }

    public static Set<String> getAllDelimiters() {
        return Stream.of(
                        DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue
                )
                .collect(Collectors.toSet());
    }
}
