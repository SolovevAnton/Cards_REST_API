package com.solovev.dao;

import com.solovev.dto.DTO;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Interface to perform CRUD operations
 *
 * @param <T> object type to perform crud on
 */
public interface DAO<T extends DTO> {
    /**
     * Gets object by its id in db
     *
     * @param id to get object
     * @return object if object with this id exists in DB empty optional otherwise
     */
    Optional<T> get(long id);

    /**
     * @return all rows in the table empty collection if nothing was found
     */
    Collection<T> get();

    /**
     * Gets single object by matching single param
     *
     * @param paramName name of the param to match
     * @param param     value to match
     * @return single result, or throws if result is more than one
     */
    <U> Optional<T> getObjectByParam(String paramName, U param);

    /**
     * Gets one object matching given params;
     * Empty Map will result in getting all entities     *
     *
     * @param paramNamesAndValues map of param names and theirs values, analog of WHERE a="A" AND b="B" AND...
     * @return matching object or empty if none match
     */
    Optional<T> getObjectByParam(Map<String, Object> paramNamesAndValues);

    /**
     * Gets collection of objects matching single param
     *
     * @param paramName name of the param to match
     * @param param     value to match
     * @return list of results, matching the criteria
     */
    <U> Collection<T> getObjectsByParam(String paramName, U param);

    /**
     * Gets all object matching given params
     *
     * @param paramNamesAndValues map of param names and theirs values, analog of WHERE a="A" AND b="B" AND...
     * @return matching objects
     */
    Collection<T> getObjectsByParam(Map<String, Object> paramNamesAndValues);

    /**
     * Adds elem in DB
     *
     * @param elem to add
     * @return true if added successfully, false otherwise
     * @throws IllegalArgumentException in case of constraint violation
     */
    boolean add(T elem) throws IllegalArgumentException;

    /**
     * Deletes object from DB
     *
     * @param id of the object
     * @return Optional of deleted object, or empty optional if there is no object with this id in DB
     */
    Optional<T> delete(long id);

    /**
     * Updates object with the id of the given object
     *
     * @param elem to update with
     * @return true if update was successful
     */
    boolean update(T elem);

}
