/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.model.Member;

import java.util.List;

/**
 * The MemberService class.
 */
public final class MemberService {

    /**
     * The member dao.
     */
    private final MemberDao memberDao;

    /**
     * Instantiates a new Member service.
     *
     * @param memberDao the member dao
     */
    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    /**
     * Registers a new member.
     *
     * @param member the member
     * @return the registered member
     */
    public Member register(Member member) {
        memberDao.create(member);
        return member;
    }

    /**
     * Finds all members.
     *
     * @return the list of members
     */
    public List<Member> findAll() {
        return memberDao.findAll();
    }
}
