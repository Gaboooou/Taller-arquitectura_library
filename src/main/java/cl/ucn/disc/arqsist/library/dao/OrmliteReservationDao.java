/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.support.ConnectionSource;

/**
 * The OrmliteReservationDao class.
 */
public final class OrmliteReservationDao extends BaseDao<Reservation> implements ReservationDao {
    /**
     * Instantiates a new Ormlite reservation dao.
     *
     * @param connectionSource the connection source
     */
    public OrmliteReservationDao(ConnectionSource connectionSource) {
        super(connectionSource, Reservation.class);
    }
}
