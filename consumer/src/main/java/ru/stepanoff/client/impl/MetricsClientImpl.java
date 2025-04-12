package ru.stepanoff.client.impl;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.MetricsClient;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsClientImpl implements MetricsClient {
    private final AtomicReference<BigInteger> numberOfSendMessages = new AtomicReference<>(BigInteger.ZERO);
    private final AtomicReference<BigInteger> numberOfErrors = new AtomicReference<>(BigInteger.ZERO);

    private final CollectorRegistry collectorRegistry;
    private final MeterRegistry meterRegistry;
    private Histogram kafkaMessageHistogram;

    @PostConstruct
    private void init() {
        Gauge.builder("numberOfSendMessages", numberOfSendMessages::get)
                .description("Количество отправленных сообщений")
                .register(meterRegistry);

        Gauge.builder("numberOfErrors", numberOfErrors::get)
                .description("Количество ошибок")
                .register(meterRegistry);

        // sum by (le) (rate(kafkaMessageHistogram_Millis_bucket[1m]))
        kafkaMessageHistogram = Histogram.build()
                .name("kafkaMessageHistogram")
                .help("Time of message to be read.")
                .buckets(1, 5, 25, 50, 100, 250, 500, 750, 1000, 2500, 5000, 10000)
                .unit("Millis")
                .register(collectorRegistry);
    }

    @Override
    public void incrementNumberOfErrors() {
        numberOfErrors.set(numberOfErrors.get().add(BigInteger.ONE));
    }

    @Override
    public <K, V> void sendKafkaRecordInfo(ConsumerRecord<K, V> consumerRecord) {
        double recordAge = Math.max(0.0, (double)System.currentTimeMillis() - consumerRecord.timestamp());
        log.debug(String.valueOf(recordAge));
        kafkaMessageHistogram.observe(recordAge);
    }

    @Override
    public void incrementNumberOfSentMessagesToProcessor() {
        numberOfSendMessages.updateAndGet(current -> current.add(BigInteger.ONE));
    }
}
