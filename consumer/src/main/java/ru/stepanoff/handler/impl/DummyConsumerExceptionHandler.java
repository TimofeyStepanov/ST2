package ru.stepanoff.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.MetricsClient;
import ru.stepanoff.handler.ConsumerExceptionHandler;

@Slf4j
@Component
public class DummyConsumerExceptionHandler implements ConsumerExceptionHandler {

    @Override
    public void handle(Throwable throwable) {
        log.error("Ошибка {}", throwable.getMessage());
    }
}
