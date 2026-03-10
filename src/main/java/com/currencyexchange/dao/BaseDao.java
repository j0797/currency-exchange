package com.currencyexchange.dao;

import com.currencyexchange.exception.AlreadyExistsException;
import com.currencyexchange.exception.DatabaseException;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T, ID> {
    Optional<T> findById(ID id) throws DatabaseException;

    List<T> findAll() throws  DatabaseException;

    T save(T entity) throws DatabaseException, AlreadyExistsException;
}