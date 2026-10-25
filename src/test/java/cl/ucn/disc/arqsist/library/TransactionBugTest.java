/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteBookDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteLoanDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteMemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests that a failed checkout rolls back any inventory changes and leaves no partial state.
 */
class TransactionBugTest {

    /**
     * The book dao.
     */
    private BookDao bookDao;

    /**
     * The loan service.
     */
    private LoanService loanService;

    /**
     * Sets up the in-memory database and services before each test.
     *
     * @throws Exception if a database error occurs
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        BookService bookService = new BookService(bookDao);
        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        loanService = new LoanService(loanDao, memberDao, bookService, clock);
    }

    /**
     * Verifies that a checkout for a non-existent member throws NotFoundException and does not decrement availableCopies.
     *
     * @throws Exception if a database error occurs
     */
    @Test
    void checkoutLeavesNoPartialStateOnFailure() throws Exception {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2);
        bookDao.create(book);

        assertThrows(NotFoundException.class, () -> loanService.checkout(9999, book.getId()));

        Book reloaded = bookDao.findById(book.getId());
        assertEquals(reloaded.getTotalCopies(), reloaded.getAvailableCopies(),
                "availableCopies was decremented even though the loan was never created");
    }
}
