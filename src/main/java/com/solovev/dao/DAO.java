package com.solovev.dao;

import java.util.Optional;

/**
 * Interface to perform CRUD operations
 * @param <T>
 */
public interface DAO<T> {
    /**
     * Gets object by its id in db
     * @param id to get object
     * @return object if object with this id exists in DB empty optional otherwise
     */
    Optional<T> get(long id);
    /**
     * Adds elem in DB
     * @param elem to add
     * @return true if added successfully, false otherwise
     */
    boolean add(T elem);

}
