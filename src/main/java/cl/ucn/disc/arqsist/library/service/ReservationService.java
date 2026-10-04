/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * The ReservationService class.
 */
public final class ReservationService {

    /**
     * The reservation dao.
     */
    private final ReservationDao reservationDao;
    
    /**
     * The book dao.
     */
    private final BookDao bookDao;
    
    /**
     * The member dao.
     */
    private final MemberDao memberDao;
    
    /**
     * The loan dao.
     */
    private final LoanDao loanDao;

    /**
     * Instantiates a new Reservation service.
     *
     * @param reservationDao the reservation dao
     * @param bookDao        the book dao
     * @param memberDao      the member dao
     * @param loanDao        the loan dao
     */
    public ReservationService(ReservationDao reservationDao, BookDao bookDao, MemberDao memberDao, LoanDao loanDao) {
        this.reservationDao = reservationDao;
        this.bookDao = bookDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    /**
     * Reserves a book for a member.
     *
     * @param bookId   the book id
     * @param memberId the member id
     * @return the reservation
     */
    public Reservation reserve(int bookId, int memberId) {
        Book book = bookDao.findById(bookId);
        Member member = memberDao.findById(memberId);
        Reservation reservation = new Reservation(member, book, LocalDate.now());
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Finds all reservations.
     *
     * @return the list of reservations
     */
    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    /**
     * Fulfills a reservation.
     *
     * @param reservationId the reservation id
     * @return the loan
     */
    public Loan fulfill(int reservationId) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null || reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation not available");
        }

        reservation.setFulfilled(true);
        reservationDao.update(reservation);

        LocalDate dueDate = LoanPolicy.dueDate(LocalDate.now());
        Loan loan = new Loan(reservation.getMember(), reservation.getBook(), LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
