/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.time.Clock;
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
     * The member dao.
     */
    private final MemberDao memberDao;

    /**
     * The loan dao.
     */
    private final LoanDao loanDao;

    /**
     * The book service.
     */
    private final BookService bookService;

    /**
     * The clock used to obtain the current date.
     */
    private final Clock clock;

    /**
     * Instantiates a new Reservation service.
     *
     * @param reservationDao the reservation dao
     * @param memberDao      the member dao
     * @param loanDao        the loan dao
     * @param bookService    the book service
     * @param clock          the clock
     */
    public ReservationService(ReservationDao reservationDao, MemberDao memberDao, LoanDao loanDao, BookService bookService, Clock clock) {
        this.reservationDao = reservationDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Reserves a book for a member.
     *
     * @param bookId   the book id
     * @param memberId the member id
     * @return the reservation
     */
    public Reservation reserve(int bookId, int memberId) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }

        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        Reservation reservation = new Reservation(member, book, LocalDate.now(clock));
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
        if (reservation == null) {
            throw new NotFoundException("Reservation not found: " + reservationId);
        }

        if (reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation already fulfilled: " + reservationId);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate dueDate = LoanPolicy.dueDate(today);

        return reservationDao.transaction(() -> {
            reservation.setFulfilled(true);
            reservationDao.update(reservation);

            int bookId = reservation.getBook().getId();
            bookService.borrow(bookId);

            Loan loan = new Loan(reservation.getMember(), bookService.findById(bookId), today, dueDate);
            loanDao.create(loan);
            return loan;
        });
    }
}
