package ru.stepanoff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;
import ru.stepanoff.constants.FieldType;
import ru.stepanoff.document.ConsumerRule;
import ru.stepanoff.document.ProcessorRule;
import ru.stepanoff.document.ProducerRule;
import ru.stepanoff.dto.*;
import ru.stepanoff.dto.deserializer.ConsumerRuleDTODeserializer;
import ru.stepanoff.dto.deserializer.ProcessorRuleDTODeserializer;
import ru.stepanoff.dto.deserializer.ProducerRuleDTODeserializer;
import ru.stepanoff.repository.ConsumerRepository;
import ru.stepanoff.repository.ProcessorRepository;
import ru.stepanoff.repository.ProducerRepository;
import ru.stepanoff.service.ManagerService;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManagerApplicationTests {
    private static final String MONGO_DB_NAME = "test-database";
    private static final String CONSUMER_MONGO_COLLECTION_NAME = "consumer";
    private static final String PROCESSOR_MONGO_COLLECTION_NAME = "processor";
    private static final String PRODUCER_MONGO_COLLECTION_NAME = "producer";


    private static final String KAFKA_CONSUMER_IN_TOPIC = "consumer_in_topic";
    private static final String KAFKA_PROCESSOR_IN_TOPIC = "processor_in_topic";
    private static final String KAFKA_PRODUCER_IN_TOPIC = "producer_in_topic";

    private static final String KAFKA_AUTO_OFFSET_RESET = "earliest";

    private static final String KAFKA_CONSUMER_OUT_TOPIC = "consumer_out_topic";
    private static final String KAFKA_PROCESSOR_OUT_TOPIC = "processor_out_topic";
    private static final String KAFKA_PRODUCER_OUT_TOPIC = "producer_out_topic";

    private static final Set<String> ALL_KAFKA_TOPICS = Set.of(
            KAFKA_CONSUMER_IN_TOPIC, KAFKA_PROCESSOR_IN_TOPIC, KAFKA_PRODUCER_IN_TOPIC,
            KAFKA_CONSUMER_OUT_TOPIC, KAFKA_PROCESSOR_OUT_TOPIC, KAFKA_PRODUCER_OUT_TOPIC
    );
    private static final short KAFKA_REPLICA_FACTOR = 1;
    private static final int KAFKA_PARTITIONS = 1;

    private static MongoClient mongoClient;
    private static AdminClient adminClient;

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private ManagerService managerService;


    @Autowired
    private ObjectMapper objectMapper;


    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getConnectionString() + "/" + MONGO_DB_NAME);

        registry.add("manager.kafka.bootstrapServices", kafka::getBootstrapServers);
        registry.add("manager.kafka.groupId", UUID::randomUUID);
        registry.add("manager.kafka.autoOffsetReset", () -> KAFKA_AUTO_OFFSET_RESET);

        registry.add("manager.kafka.consumerInTopic", () -> KAFKA_CONSUMER_IN_TOPIC);
        registry.add("manager.kafka.processorInTopic", () -> KAFKA_PROCESSOR_IN_TOPIC);
        registry.add("manager.kafka.producerInTopic", () -> KAFKA_PRODUCER_IN_TOPIC);

        registry.add("manager.kafka.consumerOutTopic", () -> KAFKA_CONSUMER_OUT_TOPIC);
        registry.add("manager.kafka.processorOutTopic", () -> KAFKA_PROCESSOR_OUT_TOPIC);
        registry.add("manager.kafka.producerOutTopic", () -> KAFKA_PRODUCER_OUT_TOPIC);
    }

    @BeforeAll
    static void createMongo() {
        mongoClient = MongoClients.create(mongoDBContainer.getConnectionString());
    }

    @BeforeEach
    void createMongoDBCollection() {
        MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);
        database.createCollection(CONSUMER_MONGO_COLLECTION_NAME);
        database.createCollection(PROCESSOR_MONGO_COLLECTION_NAME);
        database.createCollection(PRODUCER_MONGO_COLLECTION_NAME);
    }

    @AfterEach
    void clearMongoDB() {
        mongoClient.getDatabase(MONGO_DB_NAME).drop();
    }

    @AfterAll
    static void closeMongo() {
        mongoClient.close();
    }

    @BeforeAll
    static void createKafkaTopics() throws ExecutionException, InterruptedException {
        adminClient = AdminClient.create(ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()));
        Set<String> existingTopics = adminClient.listTopics().names().get();
        ALL_KAFKA_TOPICS
                .stream()
                .filter(topic -> !existingTopics.contains(topic))
                .forEach(t -> {
                    try {
                        adminClient.createTopics(List.of(new NewTopic(t, KAFKA_PARTITIONS, KAFKA_REPLICA_FACTOR))).all().get(15, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @AfterAll
    static void closeKafka() {
        adminClient.close();
    }

    /**
     * Проверяет готовность MongoDB
     */
    @Test
    void testStartMongoDB() {
        assertTrue(mongoDBContainer.isRunning());
    }

    /**
     * Проверяет готовность Kafka
     */
    @Test
    void testStartKafka() {
        assertTrue(kafka.isRunning());
    }

    /**
     * Проверить, что Кафка пишет и читает из топика
     */
    @Test
    @Disabled
    void testKafkaReadAndWrite() throws ExecutionException, InterruptedException, TimeoutException {
        var kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "tc--" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );
        kafkaConsumer.subscribe(List.of(KAFKA_CONSUMER_IN_TOPIC));

        Thread.sleep(10000);

        var kafkaProducer = new KafkaProducer<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                new StringSerializer(),
                new StringSerializer()
        );
        kafkaProducer.send(new ProducerRecord<>(KAFKA_CONSUMER_IN_TOPIC, "aaaa")).get();

        Thread.sleep(10000);

        ConsumerRecords<String, String> consumerRecords = getConsumerRecordsOutputTopic(kafkaConsumer, 10, 1);
        Assertions.assertEquals(1, consumerRecords.count());
    }

    /**
     * Тест проверят данные, которые запишутся в Кафку, если в сервис поступает первая корректная конфигурация
     */
    @Test
    @Disabled
    void testCorrectConfigurationForProcessor() throws Exception {
        try (
                KafkaConsumer<String, ConsumerRuleDTO> consumerConsumer = createKafkaConsumerForConsumer();
                KafkaConsumer<String, ProcessorRuleDTO> processorConsumer = createKafkaConsumerForProcessor();
                KafkaConsumer<String, ProducerRuleDTO> producerConsumer = createKafkaConsumerForProducer();
        ) {
            ConfigurationDTO configurationDTO = getFirstCorrectConfiguration();
            managerService.updateConfiguration(configurationDTO);


            ConsumerRecords<String, ConsumerRuleDTO> consumerRecords = getConsumerRecordsOutputTopic(consumerConsumer, 10, 1);
            ConsumerRecords<String, ProcessorRuleDTO> processorRecords = getConsumerRecordsOutputTopic(processorConsumer, 10, 1);
            ConsumerRecords<String, ProducerRuleDTO> producerRecords = getConsumerRecordsOutputTopic(producerConsumer, 10, 1);

            Assertions.assertEquals(1, consumerRecords.count());
            Assertions.assertEquals(1, processorRecords.count());
            Assertions.assertEquals(1, producerRecords.count());
        }
    }

    /**
     * Тест проверят поведение менеджера, если было получено сообщение GIVE_ME по Кафке от processor
     */
    @Test
    @Disabled
    void testRuleRepeatKafkaMessageFromProcessor() {

    }

    /**
     * Тест проверят запись в Монго, если было получено сообщение SOS по Кафке от consumer
     */
    @Test
    @Disabled
    void testSOSKafkaMessageFromConsumer() {

    }


    /**
     * Тест проверят данные, которые запишутся в Монго, если в сервис поступает первая корректная конфигурация
     */
    @Test
    void testMongoForFirstCorrectConfiguration() throws Exception {
        ConfigurationDTO configurationDTO = getFirstCorrectConfiguration();
        managerService.updateConfiguration(configurationDTO);

        Optional<ConsumerRule> consumerRule = consumerRepository.findTopByOrderByCreatedAtDesc();
        Assertions.assertTrue(consumerRule.isPresent());

        Optional<ProcessorRule> processorRule = processorRepository.findTopByOrderByCreatedAtDesc();
        Assertions.assertTrue(processorRule.isPresent());

        Optional<ProducerRule> producerRule = producerRepository.findTopByOrderByCreatedAtDesc();
        Assertions.assertTrue(producerRule.isPresent());
    }


    private KafkaConsumer<String, ConsumerRuleDTO> createKafkaConsumerForConsumer() {
        var kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
                ),
                new StringDeserializer(),
                new ConsumerRuleDTODeserializer()
        );
        kafkaConsumer.subscribe(List.of(KAFKA_CONSUMER_OUT_TOPIC));
        return kafkaConsumer;
    }

    private KafkaConsumer<String, ProcessorRuleDTO> createKafkaConsumerForProcessor() {
        var kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
                ),
                new StringDeserializer(),
                new ProcessorRuleDTODeserializer()
        );
        kafkaConsumer.subscribe(List.of(KAFKA_PROCESSOR_OUT_TOPIC));
        return kafkaConsumer;
    }

    private KafkaConsumer<String, ProducerRuleDTO> createKafkaConsumerForProducer() {
        var kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "tc" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
                ),
                new StringDeserializer(),
                new ProducerRuleDTODeserializer()
        );
        kafkaConsumer.subscribe(List.of(KAFKA_PRODUCER_OUT_TOPIC));
        return kafkaConsumer;
    }

    private ConfigurationDTO getFirstCorrectConfiguration() {
        InputTopicDTO inputTopicDTO1 = new InputTopicDTO();
        inputTopicDTO1.setAlias("home_router");
        inputTopicDTO1.setTopicNames(Set.of("home_router_1", "home_router_2"));
        inputTopicDTO1.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, true, false),
                new TopicFieldDTO("event_time", FieldType.LONG, false, false),
                new TopicFieldDTO("state", FieldType.STRING, false, false)
        ));

        InputTopicDTO inputTopicDTO2 = new InputTopicDTO();
        inputTopicDTO2.setAlias("home_clickstream");
        inputTopicDTO2.setTopicNames(Set.of("home_clickstream_1", "home_clickstream_2"));
        inputTopicDTO2.setTopicFields(Set.of(
                new TopicFieldDTO("id_device", FieldType.STRING, true, false),
                new TopicFieldDTO("event_time", FieldType.LONG, false, false),
                new TopicFieldDTO("id_session", FieldType.STRING, true, false)
        ));

        InputTopicDTO inputTopicDTO3 = new InputTopicDTO();
        inputTopicDTO3.setAlias("analytics_clickstream");
        inputTopicDTO3.setTopicNames(Set.of("analytics_clickstream"));
        inputTopicDTO3.setTopicFields(Set.of(
                new TopicFieldDTO("id_session", FieldType.STRING, true, false),
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

    private <V> ConsumerRecords<String, V> getConsumerRecordsOutputTopic(KafkaConsumer<String, V> consumer, int retry, int timeoutSeconds) {
        log.info("Start reading messages from kafka");
        boolean state = false;
        try {
            while (!state && retry > 0) {
                ConsumerRecords<String, V> consumerRecords = consumer.poll(Duration.ofMillis(1000));
                if (consumerRecords.isEmpty()) {
                    log.info("Remaining attempts {}", retry);
                    retry--;
                    Thread.sleep(timeoutSeconds * 1000L);
                } else {
                    log.info("Read messages {}", consumerRecords.count());
                    return consumerRecords;
                }
            }
        } catch (InterruptedException ex) {
            log.error("Interrupt read messages", ex);
        }
        return ConsumerRecords.empty();
    }
}
