
/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */


package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.CrudDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public final class OrmliteReservationDao extends BaseDao<Reservation> implements ReservationDao {
    public OrmliteReservationDao(ConnectionSource connectionSource) {
        super(connectionSource, Reservation.class);
    }
}
