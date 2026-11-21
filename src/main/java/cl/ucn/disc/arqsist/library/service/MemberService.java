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

    /**
     * Find a member by ID.
     *
     * @param id the member ID
     * @return the member
     * @throws NotFoundException if the member does not exist
     */
    public Member findById(int id) {
        // ORMLite returns null when the row does not exist.
        Member member = memberDao.findById(id);
        if (member == null) {
            throw new NotFoundException("Member not found: " + id);
        }
        return member;
    }

    /**
     * Search members whose name contains the given fragment.
     *
     * @param query the search fragment
     * @return matching members ordered by ID ascending
     * @throws IllegalArgumentException if the query is null or blank
     */
    public List<Member> search(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Search query must not be blank");
        }
        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Search query must not be blank");
        }
        return memberDao.findByNameContaining(trimmed);
    }
}
