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
     * Gets single object by matching single param
     * @param paramName name of the param to match
     * @param param value to match
     * @return single result, or throws if result is more than one
     */
    <U> Optional<T> getObjectByParam(String paramName, U param);

    /**
     * Gets collection of objects matching single param
     * @param paramName name of the param to match
     * @param param value to match
     * @return list of results, matching the criteria
     */
    <U> Collection<T> getObjectsByParam(String paramName, U param);

    Optional<T> getObjectByParams(String[] paramNames, Object[] params);

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

    /**
     * Updates object with the id of the given object
     * @param elem to update with
     * @return true if update was successful
     */
    boolean update(T elem);

}
