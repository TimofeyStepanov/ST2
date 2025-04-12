package ru.stepanoff.client.impl;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.MetricsClient;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class MetricsClientImpl implements MetricsClient {
    private final AtomicReference<BigInteger> numberOfSendMessages = new AtomicReference<>(BigInteger.ZERO);

    private final AtomicReference<Boolean> areDDLScriptsProcessedCorrectly = new AtomicReference<>(false);

    private final MeterRegistry meterRegistry;

    @PostConstruct
    private void init() {
        Gauge.builder("numberOfSendMessages", numberOfSendMessages::get)
                .description("Количество отправленных сообщений")
                .register(meterRegistry);

        Gauge.builder(
                        "ddlScriptsProcessedCorrectly",
                        () -> Boolean.TRUE.equals(areDDLScriptsProcessedCorrectly.get()) ? 1 : 0
                )
                .description("DDL скрипты отработаны верно (1) или нет (0)")
                .register(meterRegistry);

    }

    @Override
    public void incrementNumberOfSentMessagesToProducer() {
        numberOfSendMessages.updateAndGet(current -> current.add(BigInteger.ONE));
    }

    @Override
    public void sendDDLScriptsInfo(boolean ddlScriptsProcessedCorrectly) {
        areDDLScriptsProcessedCorrectly.set(areDDLScriptsProcessedCorrectly.get());
    }
}
