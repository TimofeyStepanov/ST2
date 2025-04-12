package ru.stepanoff.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.stepanoff.converter.Converter;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.InputTopicDTO;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.helper.SqlRequestCreatorHelper;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConfigurationDTOToProcessorRuleConverter implements Converter<ConfigurationDTO, ProcessorRuleDTO> {

    private final SqlRequestCreatorHelper sqlRequestCreatorHelper;

    @Override
    public ProcessorRuleDTO convert(ConfigurationDTO configuration) {
        ProcessorRuleDTO processorRuleDTO = new ProcessorRuleDTO();

        Map<String, String> topicAliasAndItsInsertRequest = sqlRequestCreatorHelper.createInsertRequestsForEachTopicInConfiguration(configuration);
        Map<String, String> topicAliasAndItsSelectRequest = sqlRequestCreatorHelper.createSelectRequestsForEachTopicInConfiguration(configuration);
        Map<String, ProcessorRuleDTO.TopicInfo> topicAliasAndItsInfo = new HashMap<>();
        for (InputTopicDTO inputTopic : configuration.getInputKafkaInfo().getInputTopics()) {
            topicAliasAndItsInfo.put(
                    inputTopic.getAlias(),
                    new ProcessorRuleDTO.TopicInfo(
                            topicAliasAndItsInsertRequest.get(inputTopic.getAlias()),
                            topicAliasAndItsSelectRequest.get(inputTopic.getAlias()),
                            inputTopic.getTableName()
                    )
            );
        }
        processorRuleDTO.setTopicAliasAndItsInfo(topicAliasAndItsInfo);

        processorRuleDTO.setScriptsDDL(sqlRequestCreatorHelper.createDDLRequests(configuration));
        return processorRuleDTO;
    }
}
