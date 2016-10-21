package ua.softgroup.matrix.server.service;

import java.util.Collection;

public interface GeneralEntityService<T> {

    /**
     * Returns an entity by the given primary {@code id}.
     *
     * @param id entity's primary key
     * @return the entity with the given {@code id}
     */
    T getById(Long id);

    /**
     * Saves or update an entity in a data source.
     *
     * @param entity entity's instance
     * @return the persisted entity
     */
    T save(T entity);

    /**
     * Deletes an entity from a data source.
     *
     * @param entity entity's instance
     */
    void delete(T entity);

    /**
     * Deletes an entity with the given id from a data source.
     *
     * @param id entity's id
     */
    void delete(Long id);

    /**
     * Returns all entities.
     *
     * @return a list of entities
     */
    Collection<T> getAll();

    /**
     * Checks entity existence by the given primary {@code id}.
     *
     * @param id entity's primary key
     * @return {@code true} if an entity with the given {@code id} exists; {@code false} otherwise
     */
    boolean isExist(Long id);

}
