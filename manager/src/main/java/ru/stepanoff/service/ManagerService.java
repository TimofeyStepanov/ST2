package ru.stepanoff.service;

import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.service.exception.ServiceUpdatingException;

import java.io.Closeable;
import java.util.Optional;

public interface ManagerService {

    /**
     * Обновляет правила работы для микросервисов
     *
     * @param configurationDTO правила работы для микросервисов
     */
    void updateConfiguration(ConfigurationDTO configurationDTO) throws ServiceUpdatingException;

    /**
     * Обновляет правила работы для потребителя последней конфигурацией, если таковая есть
     */
    void updateConsumerWithTheLatestConfigurationIfItExists();

    /**
     * Обновляет правила работы для процессора последней конфигурацией, если таковая есть
     */
    void updateProcessorWithTheLatestConfigurationIfItExists();

    /**
     * Обновляет правила работы для продюсера последней конфигурацией, если таковая есть
     */
    void updateProducerWithTheLatestConfigurationIfItExists();

    /**
     * Останавливает работу потребителя
     */
    void stopConsumer();

    /**
     * Получение правила работы Consumer
     */
    Optional<ConsumerRuleDTO> getConsumerRule();

    /**
     * Получение правила работы Processor
     */
    Optional<ProcessorRuleDTO> getProcessorRule();

    /**
     * Получение правила работы Producer
     */
    Optional<ProducerRuleDTO> getProducerRule();
}
