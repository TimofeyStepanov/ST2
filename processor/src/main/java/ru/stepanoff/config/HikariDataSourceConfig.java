package ru.stepanoff.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HikariDataSourceConfig {

    @Bean
    public HikariDataSource getHikariDataSource(@Value("${processor.db.url}") String url,
                                                @Value("${processor.db.driverName}") String driverName) {
        HikariConfig dataBaseConfig = new HikariConfig();
        dataBaseConfig.setJdbcUrl(url);
        dataBaseConfig.setDriverClassName(driverName);
        return new HikariDataSource(dataBaseConfig);
    }
}
