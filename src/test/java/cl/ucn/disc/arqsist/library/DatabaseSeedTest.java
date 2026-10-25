/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteBookDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteLoanDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Database.seedIfEmpty().
 */
class DatabaseSeedTest {

    /**
     * Verifies initial seed records, inventory consistency, and idempotency.
     *
     * @throws Exception if a database error occurs
     */
    @Test
    void seedPopulatesConsistentDataAndIsIdempotent() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        db.seedIfEmpty();

        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        ReservationDao reservationDao = new OrmliteReservationDao(db.connectionSource());

        List<Loan> loans = loanDao.findAll();
        List<Reservation> reservations = reservationDao.findAll();

        assertFalse(loans.isEmpty());
        assertFalse(reservations.isEmpty());

        LocalDate today = LocalDate.now();
        long overdueCount = loans.stream()
                .filter(l -> !l.isReturned() && l.getDueDate().isBefore(today))
                .count();
        assertEquals(1, overdueCount);

        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                Book book = bookDao.findById(loan.getBook().getId());
                assertTrue(book.getAvailableCopies() < book.getTotalCopies());
            }
        }

        int loanCountBefore = loans.size();
        int reservationCountBefore = reservations.size();

        db.seedIfEmpty();

        assertEquals(loanCountBefore, loanDao.findAll().size());
        assertEquals(reservationCountBefore, reservationDao.findAll().size());
    }
}
