/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteBookDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteLoanDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteMemberDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that checkout and reservation fulfillment use the same loan period policy.
 */
class DueDateDuplicationTest {

    /**
     * The loan service.
     */
    private LoanService loanService;

    /**
     * The reservation service.
     */
    private ReservationService reservationService;

    /**
     * The test book.
     */
    private Book book;

    /**
     * The test member.
     */
    private Member member;

    /**
     * Sets up the in-memory database, services, and test entities before each test.
     *
     * @throws Exception if a database error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        ReservationDao reservationDao = new OrmliteReservationDao(db.connectionSource());
        BookService bookService = new BookService(bookDao);
        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        loanService = new LoanService(loanDao, memberDao, bookService, clock);
        reservationService = new ReservationService(reservationDao, memberDao, loanDao, bookService, clock);

        book = new Book("Design Patterns", "Gamma et al.", "9780201633610", 2);
        bookDao.create(book);
        member = new Member("Grace Hopper", "grace@example.com");
        memberDao.create(member);
    }

    /**
     * Verifies that checkout and fulfill produce the exact same due date.
     *
     * @throws Exception if an error occurs during checkout or fulfillment
     */
    @Test
    void checkoutAndFulfillUseTheSameLoanPeriod() throws Exception {
        Loan fromCheckout = loanService.checkout(member.getId(), book.getId());

        Reservation reservation = reservationService.reserve(book.getId(), member.getId());
        Loan fromFulfill = reservationService.fulfill(reservation.getId());

        assertEquals(fromCheckout.getDueDate(), fromFulfill.getDueDate(),
                "the same kind of loan should have the same due date");
    }
}
