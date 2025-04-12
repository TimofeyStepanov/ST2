package ru.stepanoff.client.impl;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.distribution.Histogram;
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

    private final MeterRegistry meterRegistry;

    @PostConstruct
    private void init() {
        Gauge.builder("numberOfSendMessages", numberOfSendMessages::get)
                .description("Количество отправленных сообщений")
                .register(meterRegistry);
    }

    @Override
    public void incrementNumberOfSentMessagesToUser() {
        numberOfSendMessages.updateAndGet(current -> current.add(BigInteger.ONE));
    }
}
