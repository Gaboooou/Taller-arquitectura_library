/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * The LoanService class.
 */
public final class LoanService {

    /**
     * The constant DUE_DAYS.
     */
    public static final int DUE_DAYS = 21;

    /**
     * The loan dao.
     */
    private final LoanDao loanDao;

    /**
     * The book dao.
     */
    private final BookDao bookDao;

    /**
     * Instantiates a new Loan service.
     *
     * @param loanDao the loan dao
     * @param bookDao the book dao
     */
    public LoanService(LoanDao loanDao, BookDao bookDao) {
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    /**
     * Finds all loans.
     *
     * @return the list of loans
     */
    public List<Loan> findAll() {
        return loanDao.findAll();
    }

    /**
     * Returns a loan.
     *
     * @param loanId the loan id
     * @return the loan
     */
    public Loan returnLoan(int loanId) {
        Loan loan = loanDao.findById(loanId);
        if (loan == null || loan.isReturned()) {
            return loan;
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        LocalDate due = loan.getDueDate();
        LocalDate today = LocalDate.now();
        if (today.isAfter(due)) {
            long daysOverdue = ChronoUnit.DAYS.between(due, today);
            loan.setOverdueFee(daysOverdue * LoanPolicy.FEE_PER_DAY);
        }

        loanDao.update(loan);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDao.update(book);

        return loan;
    }

    /**
     * Gets overdue loans.
     *
     * @return the list of overdue loans
     * @throws SQLException the sql exception
     */
    public List<Loan> overdueLoans() throws SQLException {
        return loanDao.findAll().stream().filter(Loan::isOverdue).toList();
    }
}
