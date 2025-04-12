package ru.stepanoff;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
//@Disabled
class ProcessorApplicationTests {

    @Autowired
    private RedisTemplate<String, List<String>> redisTemplate;

    private static final RedisContainer redisContainer1 = new RedisContainer(DockerImageName.parse("redis:6.2.6"));
    private static final RedisContainer redisContainer2 = new RedisContainer(DockerImageName.parse("redis:6.2.6"));

    private static final ClickHouseContainer clickHouseContainer = new ClickHouseContainer("clickhouse/clickhouse-server:23.3.8.21-alpine");

    @Test
    void contextLoads() {
    }

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        //registry.add("spring.data.redis.cluster.nodes", () -> redisContainer1.getRedisURI() + "," + redisContainer2.getRedisURI());
        registry.add("processor.db.url", clickHouseContainer::getJdbcUrl);

    }


    @BeforeAll
    static void startContainers() {
        clickHouseContainer.start();
        redisContainer1.start();
        redisContainer2.start();
    }

    @AfterAll
    static void stopContainers() {
        clickHouseContainer.stop();
        redisContainer1.stop();
        redisContainer2.stop();
    }


    @Test
    void testRedisReadAndWrite() {
        redisTemplate.opsForValue().set(
                "select analytics_clickstream.host from my_database.home_router join my_database.home_clickstream on home_router.id_device = home_clickstream.id_device join my_database.analytics_clickstream on analytics_clickstream.id_session = home_clickstream.id_session where home_router.id_device = 'id_device1' format JSONEachRow",
                List.of("{\"analytics_clickstream.host\":\"host1\"}")
        );
        Optional<List<String>> cacheResult = Optional.ofNullable(redisTemplate.opsForValue().get("a"));

        Assertions.assertTrue(cacheResult.isPresent());
        Assertions.assertEquals(List.of("b", "c"), cacheResult.get());
    }

    @Test
    void testRedisUnknownValue() {
        redisTemplate.opsForValue().set("a", List.of("b", "c"));
        Optional<List<String>> cacheResult = Optional.ofNullable(redisTemplate.opsForValue().get("unknown"));

        Assertions.assertTrue(cacheResult.isEmpty());
    }
}
