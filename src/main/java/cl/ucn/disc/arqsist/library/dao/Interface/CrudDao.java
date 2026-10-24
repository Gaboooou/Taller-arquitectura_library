/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao.Interface;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The CrudDao interface.
 *
 * @param <T> the entity type parameter
 */
public interface CrudDao<T> {

    /**
     * Finds all entities.
     *
     * @return the list of entities
     */
    List<T> findAll();

    /**
     * Finds an entity by id.
     *
     * @param id the id
     * @return the entity, or null if not found
     */
    T findById(int id);

    /**
     * Creates a new entity.
     *
     * @param entity the entity
     */
    void create(T entity);

    /**
     * Updates an existing entity.
     *
     * @param entity the entity
     */
    void update(T entity);

    /**
     * Deletes an entity.
     *
     * @param entity the entity
     */
    void delete(T entity);

    /**
     * Executes a callable within a database transaction.
     *
     * @param <R>      the return type parameter
     * @param callable the callable to execute
     * @return the result of the callable
     */
    <R> R transaction(Callable<R> callable);

}
