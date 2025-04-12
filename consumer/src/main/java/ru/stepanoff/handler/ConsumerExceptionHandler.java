package ru.stepanoff.handler;

public interface ConsumerExceptionHandler {
    void handle(Throwable throwable);
}
