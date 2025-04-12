package ru.stepanoff.constants;

/**
 * Перечисление доступных сообщений в Kafka для общения с manager
 */
public enum MessagesEnum {
    SOS("SOS"), // Сообщение сингнализирует о том, что работу микросервисов нужно прикратить
    RULE_REPEAT("GIVE_ME"); // Сообщение сигнализирует о том, что необходимо продублировать правило работы микросервиса

    public final String message;

    MessagesEnum(String message) {
        this.message = message;
    }
}
