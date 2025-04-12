package ru.stepanoff.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.stepanoff.repository.StateRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class StateRepositoryImpl implements StateRepository {

    private final HikariDataSource hikariDataSource;

    @Override
    public <T> List<T> createSqlQuery(String sqlQuery, Class<T> clazz) throws SQLException {
        List<T> result;
        try (Connection connection = hikariDataSource.getConnection()) {
            DSLContext dsl = DSL.using(connection);
            log.debug("Делаю запрос в бд: {}", sqlQuery);
            result = dsl.fetch(sqlQuery).into(clazz);
            log.debug("Получил из бд: {}", result);
        }
        return result;
    }
}
