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
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test for accrued overdue fees in LoanService
 */
public class AccruedFeeTest {

    /**
     * Verifies that findAll computes accrued fees for open overdue loans in memory.
     *
     * @throws Exception if a database error occurs
     */
    @Test
    void findAllComputesAccruedFeeForOpenOverdueLoans() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());

        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        BookService bookService = new BookService(bookDao);
        LoanService loanService = new LoanService(loanDao, memberDao, bookService, clock);

        Book book = new Book("Clean Architecture", "Robert C. Martin", "9780134494166", 2);
        bookDao.create(book);
        Member member = new Member("Ada Lovelace", "ada@example.com");
        memberDao.create(member);

        Loan overdueLoan = new Loan(member, book, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 22));
        loanDao.create(overdueLoan);

        Loan activeLoan = new Loan(member, book, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 31));
        loanDao.create(activeLoan);

        List<Loan> loans = loanService.findAll();
        assertEquals(2, loans.size());
        assertEquals(24.0, loans.get(0).getOverdueFee());
        assertEquals(0.0, loans.get(1).getOverdueFee());
    }

}
