/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * The OrmliteMemberDao class.
 */
public final class OrmliteMemberDao extends BaseDao<Member> implements MemberDao {
    /**
     * Instantiates a new Ormlite member dao.
     *
     * @param connectionSource the connection source
     */
    public OrmliteMemberDao(ConnectionSource connectionSource) {
        super(connectionSource, Member.class);
    }

    /**
     * Find members whose name contains the given literal text (case-insensitive for ASCII),
     * ordered by ID ascending.
     *
     * @param query the literal substring to search for
     * @return matching members ordered by ID ascending
     */
    @Override
    public List<Member> findByNameContaining(String query) {
        try {
            QueryBuilder<Member, Integer> qb = dao.queryBuilder();
            qb.where().raw("instr(lower(name), lower(?)) > 0", new SelectArg(query));
            qb.orderBy("id", true);
            return qb.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
