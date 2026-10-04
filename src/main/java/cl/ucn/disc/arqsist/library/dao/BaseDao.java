/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.CrudDao;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * The BaseDao class.
 *
 * @param <T> the type parameter
 */
public abstract class BaseDao<T> implements CrudDao<T> {
    /**
     * The Dao.
     */
    protected final Dao<T, Integer> dao;

    /**
     * Instantiates a new Base dao.
     *
     * @param connectionSource the connection source
     * @param clazz            the clazz
     */
    protected BaseDao(ConnectionSource connectionSource, Class<T> clazz){
        try {
            this.dao = DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    @Override
    public List<T> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the t
     */
    @Override
    public T findById(int id){
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create.
     *
     * @param entity the entity
     */
    @Override
    public void create(T entity)  {
        try {
            dao.create(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update.
     *
     * @param entity the entity
     */
    @Override
    public void update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete.
     *
     * @param entity the entity
     */
    @Override
    public void delete(T entity) {
        try {
            dao.delete(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transaction r.
     *
     * @param <R>      the type parameter
     * @param callable the callable
     * @return the r
     * @throws SQLException the sql exception
     */
    public <R> R transaction(Callable<R> callable) throws SQLException {
        try {
            return TransactionManager.callInTransaction(dao.getConnectionSource(), callable);
        } catch (SQLException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw e;
        }
    }
}
