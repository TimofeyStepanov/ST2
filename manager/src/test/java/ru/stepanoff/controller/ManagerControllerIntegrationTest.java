package ru.stepanoff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.stepanoff.constants.FieldType;
import ru.stepanoff.dto.*;

import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Disabled
    void testRequestWithEmptyTriggerCondition() throws Exception {
        InputTopicDTO inputTopicDTO = new InputTopicDTO();
        inputTopicDTO.setAlias("alias");
        inputTopicDTO.setTopicNames(Set.of("aaaaa"));
        inputTopicDTO.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, true),
                new TopicFieldDTO("id_session", FieldType.STRING, false, false)
        ));

        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("aaaaa"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputTopics(Set.of(inputTopicDTO))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );
        configurationDTO.setTriggerCondition("");

        mockMvc.perform(post("/api/manager/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(configurationDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Нет информации об условии для создания триггера")));
    }


}