package ru.stepanoff.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Перечисление доступных типов полей топиков
 */
public enum FieldType {
    STRING("string"), LONG("long"), INT("int"), DOUBLE("double");

    public final String typeName;

    FieldType(String typeName) {
        this.typeName = typeName;
    }

    public static Set<String> getAllTypeNames() {
        return Arrays.stream(FieldType.values())
                .map(enumType -> enumType.typeName)
                .collect(Collectors.toSet());
    }

    public static Set<FieldType> getFieldTypesThatRequireQuotationMarks() {
        return Set.of(STRING);
    }

}
