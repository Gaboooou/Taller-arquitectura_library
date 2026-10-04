
/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.CrudDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public final class OrmliteMemberDao extends BaseDao<Member> implements MemberDao {
    public OrmliteMemberDao(ConnectionSource connectionSource) {
        super(connectionSource, Member.class);
    }

}
