/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * The LoanService class.
 */
public final class LoanService {

    /**
     * The loan dao.
     */
    private final LoanDao loanDao;

    /**
     * The member dao.
     */
    private final MemberDao memberDao;

    /**
     * The book service.
     */
    private final BookService bookService;

    /**
     * The clock used to obtain the current date.
     */
    private final Clock clock;

    /**
     * Instantiates a new Loan service with the system default clock.
     *
     * @param loanDao     the loan dao
     * @param memberDao   the member dao
     * @param bookService the book service
     */
    public LoanService(LoanDao loanDao, MemberDao memberDao, BookService bookService) {
        this(loanDao, memberDao, bookService, Clock.systemDefaultZone());
    }

    /**
     * Instantiates a new Loan service.
     *
     * @param loanDao     the loan dao
     * @param memberDao   the member dao
     * @param bookService the book service
     * @param clock       the clock
     */
    public LoanService(LoanDao loanDao, MemberDao memberDao, BookService bookService, Clock clock) {
        this.loanDao = loanDao;
        this.memberDao = memberDao;
        this.bookService = bookService;
        this.clock = clock;
    }

    /**
     * Checks out a book to a member within a transaction.
     *
     * @param memberId the member id
     * @param bookId   the book id
     * @return the created loan
     */
    public Loan checkout(int memberId, int bookId) {
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate dueDate = LoanPolicy.dueDate(today);

        return loanDao.transaction(() -> {
            bookService.borrow(bookId);
            Book borrowedBook = bookService.findById(bookId);
            Loan loan = new Loan(member, borrowedBook, today, dueDate);
            loanDao.create(loan);
            return loan;
        });
    }

    /**
     * Returns a loan within a transaction and calculates any overdue fee.
     *
     * @param loanId the loan id
     * @return the updated loan
     */
    public Loan returnLoan(int loanId) {
        Loan loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new NotFoundException("Loan not found: " + loanId);
        }
        if (loan.isReturned()) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }

        LocalDate today = LocalDate.now(clock);
        loan.setReturned(true);
        loan.setReturnDate(today);

        if (today.isAfter(loan.getDueDate())) {
            loan.setOverdueFee(accruedFee(loan, today));
        }

        return loanDao.transaction(() -> {
            loanDao.update(loan);
            bookService.returnCopy(loan.getBook().getId());
            return loan;
        });
    }

    /**
     * Gets all open loans whose due date is before today, with accrued fees populated in memory.
     *
     * @return the list of overdue loans
     */
    public List<Loan> overdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return findAll().stream()
                .filter(loan -> !loan.isReturned() && loan.getDueDate().isBefore(today))
                .toList();
    }

    /**
     * Finds all loans and populates accrued fees for open overdue loans in memory.
     *
     * @return the list of loans
     */
    public List<Loan> findAll() {
        return withAccruedFees(loanDao.findAll());
    }

    /**
     * Assigns the calculated overdue fee in memory to each open overdue loan without persisting to the database.
     *
     * @param loans the list of loans
     * @return the list of loans with accrued fees assigned in memory
     */
    private List<Loan> withAccruedFees(List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getDueDate().isBefore(today)) {
                loan.setOverdueFee(accruedFee(loan, today));
            }
        }
        return loans;
    }

    /**
     * Calculates the accrued overdue fee for a loan as of the given date.
     *
     * @param loan  the loan
     * @param today the reference date
     * @return the accrued fee
     */
    private static double accruedFee(Loan loan, LocalDate today) {
        return ChronoUnit.DAYS.between(loan.getDueDate(), today) * LoanPolicy.FEE_PER_DAY;
    }
}
