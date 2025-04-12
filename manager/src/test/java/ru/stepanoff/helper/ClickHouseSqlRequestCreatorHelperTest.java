package ru.stepanoff.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.stepanoff.constants.FieldType;
import ru.stepanoff.dto.*;
import ru.stepanoff.helper.impl.ClickHouseSqlRequestCreatorHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class ClickHouseSqlRequestCreatorHelperTest {

    @Autowired
    private ClickHouseSqlRequestCreatorHelper clickHouseSqlRequestCreatorHelper;

    @Test
    void testSelectRequestForFirstCorrectConfiguration() {
        Map<String, String> topicAndItsSelectRequestExpected = new HashMap<>();

        String selectRequestForHomeRouter =
                "select analytics_clickstream.host " +
                "from my_database.home_router " +
                "join my_database.home_clickstream_table on home_router.id_device = home_clickstream_table.id_device " +
                "join my_database.analytics_clickstream on analytics_clickstream.id_session = home_clickstream_table.id_session " +
                "where home_router.id_device = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("home_router", selectRequestForHomeRouter);

        String selectRequestForHomeClickStream =
                "select analytics_clickstream.host " +
                "from my_database.home_clickstream_table " +
                "join my_database.analytics_clickstream on analytics_clickstream.id_session = home_clickstream_table.id_session " +
                "join my_database.home_router on home_router.id_device = home_clickstream_table.id_device " +
                "where home_clickstream_table.id_device = ? and home_clickstream_table.id_session = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("home_clickstream", selectRequestForHomeClickStream);

        String selectRequestForAnalyticsClickStream =
                "select analytics_clickstream.host " +
                "from my_database.analytics_clickstream " +
                "join my_database.home_clickstream_table on analytics_clickstream.id_session = home_clickstream_table.id_session " +
                "join my_database.home_router on home_router.id_device = home_clickstream_table.id_device " +
                "where analytics_clickstream.id_session = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("analytics_clickstream", selectRequestForAnalyticsClickStream);

        ConfigurationDTO configurationDTO = getFirstCorrectConfiguration();
        Map<String, String> topicAndItsSelectRequestActual = clickHouseSqlRequestCreatorHelper.createSelectRequestsForEachTopicInConfiguration(configurationDTO);
        Assertions.assertEquals(topicAndItsSelectRequestExpected, topicAndItsSelectRequestActual);
    }

    @Test
    void testSelectRequestForFirstCorrectConfigurationWithAnotherTriggerCondition() {
        Map<String, String> topicAndItsSelectRequestExpected = new HashMap<>();

        String selectRequestForHomeRouter =
                "select analytics_clickstream.host " +
                "from my_database.home_router " +
                "join my_database.home_clickstream_table on home_router.id_device = home_clickstream_table.id_device " +
                "join my_database.analytics_clickstream on analytics_clickstream.event_time = home_clickstream_table.event_time " +
                "where home_router.id_device = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("home_router", selectRequestForHomeRouter);

        String selectRequestForHomeClickStream =
                "select analytics_clickstream.host " +
                "from my_database.home_clickstream_table " +
                "join my_database.analytics_clickstream on analytics_clickstream.event_time = home_clickstream_table.event_time " +
                "join my_database.home_router on home_router.id_device = home_clickstream_table.id_device " +
                "where home_clickstream_table.id_device = ? and home_clickstream_table.event_time = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("home_clickstream", selectRequestForHomeClickStream);

        String selectRequestForAnalyticsClickStream =
                "select analytics_clickstream.host " +
                "from my_database.analytics_clickstream " +
                "join my_database.home_clickstream_table on analytics_clickstream.event_time = home_clickstream_table.event_time " +
                "join my_database.home_router on home_router.id_device = home_clickstream_table.id_device " +
                "where analytics_clickstream.event_time = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("analytics_clickstream", selectRequestForAnalyticsClickStream);

        ConfigurationDTO configurationDTO = getFirstCorrectConfiguration();
        configurationDTO.setTriggerCondition("home_router.id_device = home_clickstream.id_device and analytics_clickstream.event_time = home_clickstream.event_time");
        Map<String, String> topicAndItsSelectRequestActual = clickHouseSqlRequestCreatorHelper.createSelectRequestsForEachTopicInConfiguration(configurationDTO);
        Assertions.assertEquals(topicAndItsSelectRequestExpected, topicAndItsSelectRequestActual);
    }

    private ConfigurationDTO getFirstCorrectConfiguration() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO();
        inputTopicDTO1.setAlias("home_router");
        inputTopicDTO1.setTableName("home_router");
        inputTopicDTO1.setTopicNames(Set.of("home_router_1", "home_router_2"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, false),
                new TopicFieldDTO("event_time", FieldType.LONG, false, false),
                new TopicFieldDTO("state", FieldType.STRING, false, false)
        ));

        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("home_clickstream");
        inputTopicDTO2.setTableName("home_clickstream_table");
        inputTopicDTO2.setTopicNames(Set.of("home_clickstream_1", "home_clickstream_2"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, false, false),
                new TopicFieldDTO("event_time", FieldType.LONG, false, false),
                new TopicFieldDTO("id_session", FieldType.STRING, false, false)
        ));

        InputTopicDTO inputTopicDTO3 = new InputTopicDTO();
        inputTopicDTO3.setAlias("analytics_clickstream");
        inputTopicDTO3.setTableName("analytics_clickstream");
        inputTopicDTO3.setTopicNames(Set.of("analytics_clickstream"));
        inputTopicDTO3.setTopicFields(Set.of(
                new TopicFieldDTO("id_session", FieldType.STRING, false, false),
                new TopicFieldDTO("host", FieldType.STRING, false, true)
        ));

        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("topic5"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputBootstrapServers("kafka:9090")
                        .inputTopics(Set.of(inputTopicDTO1, inputTopicDTO2, inputTopicDTO3))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputBootstrapServers("kafka:9090")
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );
        configurationDTO.setTriggerCondition("home_router.id_device = home_clickstream.id_device and analytics_clickstream.id_session = home_clickstream.id_session");
        return configurationDTO;
    }


    @Test
    void testSelectRequestForSecondConfiguration() {
        Map<String, String> topicAndItsSelectRequestExpected = new HashMap<>();

        String selectRequestForFirstTable =
                "select 1.a, 1.c, 2.d " +
                "from my_database.1 " +
                "join my_database.2 on 1.a = 2.d " +
                "where 1.a = 1.c and 1.a = ? and 1.c = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("one", selectRequestForFirstTable);

        String selectRequestForSecondTable =
                "select 1.a, 1.c, 2.d " +
                "from my_database.2 " +
                "join my_database.1 on 1.a = 1.c and 1.a = 2.d " +
                "where 2.d = ? " +
                "format JSONEachRow";
        topicAndItsSelectRequestExpected.put("two", selectRequestForSecondTable);

        ConfigurationDTO configurationDTO = getSecondCorrectConfiguration();
        Map<String, String> topicAndItsSelectRequestActual = clickHouseSqlRequestCreatorHelper.createSelectRequestsForEachTopicInConfiguration(configurationDTO);
        Assertions.assertEquals(topicAndItsSelectRequestExpected, topicAndItsSelectRequestActual);
    }

    private ConfigurationDTO getSecondCorrectConfiguration() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO();
        inputTopicDTO1.setAlias("one");
        inputTopicDTO1.setTableName("1");
        inputTopicDTO1.setTopicNames(Set.of("1"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO("a", FieldType.STRING, false, true),
                new TopicFieldDTO("b", FieldType.LONG, false, false),
                new TopicFieldDTO("c", FieldType.STRING, false, true)
        ));

        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("two");
        inputTopicDTO2.setTableName("2");
        inputTopicDTO2.setTopicNames(Set.of("2"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("d", FieldType.STRING, false, true),
                new TopicFieldDTO("e", FieldType.LONG, false, false),
                new TopicFieldDTO("f", FieldType.STRING, false, false)
        ));

        InputTopicDTO inputTopicDTO3 = new InputTopicDTO();
        inputTopicDTO3.setAlias("three");
        inputTopicDTO3.setTableName("3");
        inputTopicDTO3.setTopicNames(Set.of("3"));
        inputTopicDTO3.setTopicFields(Set.of(
                new TopicFieldDTO("g", FieldType.STRING, false, true),
                new TopicFieldDTO("h", FieldType.LONG, false, false),
                new TopicFieldDTO("i", FieldType.STRING, false, false)
        ));

        OutputTopicDTO outputTopicDTO = new OutputTopicDTO();
        outputTopicDTO.setTopicNames(Set.of("topic"));

        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        configurationDTO.setInputKafkaInfo(
                InputKafkaInfo.builder()
                        .inputBootstrapServers("kafka:9090")
                        .inputTopics(Set.of(inputTopicDTO1, inputTopicDTO2, inputTopicDTO3))
                        .build()
        );
        configurationDTO.setOutputKafkaInfo(
                OutputKafkaInfo.builder()
                        .outputBootstrapServers("kafka:9090")
                        .outputTopic(Set.of(outputTopicDTO))
                        .build()
        );
        configurationDTO.setTriggerCondition("1.a  = 2.d and 1.a = 1.c");

        return configurationDTO;
    }
}