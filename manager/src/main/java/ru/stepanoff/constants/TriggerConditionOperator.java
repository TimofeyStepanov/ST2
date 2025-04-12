package ru.stepanoff.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Перечисление доступных операторов в условии для создания триггера
 */
public enum TriggerConditionOperator {
    AND("and");
    public final String conditionName;

    TriggerConditionOperator(String conditionName) {
        this.conditionName = conditionName;
    }

    public static Set<String> getAllOperatorNames() {
        return Arrays.stream(TriggerConditionOperator.values())
                .map(enumType -> enumType.conditionName)
                .collect(Collectors.toSet());
    }
}
