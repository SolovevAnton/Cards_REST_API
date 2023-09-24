package com.solovev.dao;

import java.util.Collection;
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
     *
     * @return all rows in the table ampty collection if nothing was found
     */
    Collection<T> get();

    /**
     * Adds elem in DB
     * @param elem to add
     * @return true if added successfully, false otherwise
     * @throws IllegalArgumentException in case of constraint violation
     */
    boolean add(T elem) throws IllegalArgumentException;

    /**
     * Deletes object from DB
     * @param id of the object
     * @return Optional of deleted object, or empty optional if there is no object with this id in DB
     */
    Optional<T> delete(long id);
}
