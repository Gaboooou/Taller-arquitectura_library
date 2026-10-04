/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import cl.ucn.disc.arqsist.library.db.LocalDatePersister;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * The Loan class.
 */
@DatabaseTable(tableName = "loans")
public final class Loan {

    /**
     * The id.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The member.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;

    /**
     * The book.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Book book;

    /**
     * The loan date.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate loanDate;

    /**
     * The due date.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate dueDate;

    /**
     * The return date.
     */
    @DatabaseField(persisterClass = LocalDatePersister.class)
    private LocalDate returnDate;

    /**
     * The returned status.
     */
    @DatabaseField
    private boolean returned;

    /**
     * The overdue fee.
     */
    @DatabaseField
    private double overdueFee;

    /**
     * Instantiates a new empty Loan.
     */
    public Loan() {
    }

    /**
     * Instantiates a new Loan.
     *
     * @param member   the member
     * @param book     the book
     * @param loanDate the loan date
     * @param dueDate  the due date
     */
    public Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.member = member;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.overdueFee = 0.0;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the member.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets the member.
     *
     * @param member the member
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Gets the book.
     *
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book.
     *
     * @param book the book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the loan date.
     *
     * @return the loan date
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loan date.
     *
     * @param loanDate the loan date
     */
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the return date.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date.
     *
     * @param returnDate the return date
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Checks if returned.
     *
     * @return true if returned, false otherwise
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Sets the returned status.
     *
     * @param returned the returned status
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Checks if overdue.
     *
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() { 
        return !returned && LocalDate.now().isAfter(dueDate); 
    }

    /**
     * Gets the overdue fee.
     *
     * @return the overdue fee
     */
    public double getOverdueFee() {
        return overdueFee;
    }

    /**
     * Sets the overdue fee.
     *
     * @param overdueFee the overdue fee
     */
    public void setOverdueFee(double overdueFee) {
        this.overdueFee = overdueFee;
    }
}
