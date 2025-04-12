package ru.stepanoff.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Перечисление доступных знаков в условии для создания триггера
 */
public enum TriggerConditionSign {
    EQUALS("=");
    public final String signName;

    TriggerConditionSign(String signName) {
        this.signName = signName;
    }

    public static Set<String> getAllSignNames() {
        return Arrays.stream(TriggerConditionSign.values())
                .map(enumType -> enumType.signName)
                .collect(Collectors.toSet());
    }
}
