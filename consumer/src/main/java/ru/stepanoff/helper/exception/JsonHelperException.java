package ru.stepanoff.helper.exception;

public class JsonHelperException extends Exception {
    public JsonHelperException(String message) {
        super(message);
    }

    public JsonHelperException(Throwable cause) {
        super(cause);
    }
}
