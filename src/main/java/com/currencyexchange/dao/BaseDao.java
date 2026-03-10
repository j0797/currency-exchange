package com.currencyexchange.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseDao<T, ID> {
    Optional<T> findById(ID id) throws SQLException;

    List<T> findAll() throws SQLException;

    T save(T entity) throws SQLException;
}