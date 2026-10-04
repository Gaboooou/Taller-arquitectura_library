/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.model.Loan;
import com.j256.ormlite.support.ConnectionSource;

/**
 * The OrmliteLoanDao class.
 */
public final class OrmliteLoanDao extends BaseDao<Loan> implements LoanDao {
    /**
     * Instantiates a new Ormlite loan dao.
     *
     * @param connectionSource the connection source
     */
    public OrmliteLoanDao(ConnectionSource connectionSource) {
        super(connectionSource, Loan.class);
    }
}
