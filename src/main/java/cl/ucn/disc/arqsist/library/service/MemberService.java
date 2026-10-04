/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.time.LocalDate;
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
     * The book dao.
     */
    private final BookDao bookDao;
    
    /**
     * The loan dao.
     */
    private final LoanDao loanDao;

    /**
     * Instantiates a new Member service.
     *
     * @param memberDao the member dao
     * @param bookDao   the book dao
     * @param loanDao   the loan dao
     */
    public MemberService(MemberDao memberDao, BookDao bookDao, LoanDao loanDao) {
        this.memberDao = memberDao;
        this.bookDao = bookDao;
        this.loanDao = loanDao;
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
     * Checks out a book to a member.
     *
     * @param memberId the member id
     * @param bookId   the book id
     * @return the created loan
     */
    public Loan checkout(int memberId, int bookId) {
        Member member = memberDao.findById(memberId);
        Book book = bookDao.findById(bookId);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);

        LocalDate dueDate = LoanPolicy.dueDate(LocalDate.now());
        Loan loan = new Loan(member, book, LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
