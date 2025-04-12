package ru.stepanoff.repository;

import java.sql.SQLException;
import java.util.List;

public interface StateRepository {
    <T> List<T> createSqlQuery(String sql, Class<T> clazz) throws SQLException;
}
