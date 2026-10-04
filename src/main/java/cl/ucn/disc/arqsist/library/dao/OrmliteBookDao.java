/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.support.ConnectionSource;

/**
 * The OrmliteBookDao class.
 */
public final class OrmliteBookDao extends BaseDao<Book> implements BookDao {
    /**
     * Instantiates a new Ormlite book dao.
     *
     * @param connectionSource the connection source
     */
    public OrmliteBookDao(ConnectionSource connectionSource) {
        super(connectionSource, Book.class);
    }
}
