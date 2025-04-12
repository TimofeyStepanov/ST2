package ru.stepanoff.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.service.ManagerService;
import ru.stepanoff.service.exception.ServiceUpdatingException;

import java.util.Optional;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/echo")
    @Operation(summary = "Проверка работы сервиса")
    public String echo() {
        return "Hello from manager";
    }

    @PostMapping("/update")
    @Operation(summary = "Обновление правил работы системы")
    public void updateConfiguration(@RequestBody @Validated ConfigurationDTO configurationDTO) throws ServiceUpdatingException {
        managerService.updateConfiguration(configurationDTO);
    }

    @GetMapping("/consumer")
    @Operation(summary = "Получение правил работы consumer'a")
    public Optional<ConsumerRuleDTO> getConsumerRule() {
        return managerService.getConsumerRule();
    }

    @GetMapping("/processor")
    @Operation(summary = "Получение правил работы processor'a")
    public Optional<ProcessorRuleDTO> getProcessorRule() {
        return managerService.getProcessorRule();
    }

    @GetMapping("/producer")
    @Operation(summary = "Получение правил работы producer'a")
    public Optional<ProducerRuleDTO> getProducerRule() {
        return managerService.getProducerRule();
    }
}
