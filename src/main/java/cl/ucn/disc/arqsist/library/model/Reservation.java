/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import cl.ucn.disc.arqsist.library.db.LocalDatePersister;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDate;

/**
 * The Reservation class.
 */
@DatabaseTable(tableName = "reservations")
public final class Reservation {

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
     * The reserved at date.
     */
    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate reservedAt;

    /**
     * The fulfilled status.
     */
    @DatabaseField
    private boolean fulfilled;

    /**
     * Instantiates a new empty Reservation.
     */
    public Reservation() {
    }

    /**
     * Instantiates a new Reservation.
     *
     * @param member     the member
     * @param book       the book
     * @param reservedAt the reserved at date
     */
    public Reservation(Member member, Book book, LocalDate reservedAt) {
        this.member = member;
        this.book = book;
        this.reservedAt = reservedAt;
        this.fulfilled = false;
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
     * Gets the reserved at date.
     *
     * @return the reserved at date
     */
    public LocalDate getReservedAt() {
        return reservedAt;
    }

    /**
     * Sets the reserved at date.
     *
     * @param reservedAt the reserved at date
     */
    public void setReservedAt(LocalDate reservedAt) {
        this.reservedAt = reservedAt;
    }

    /**
     * Checks if fulfilled.
     *
     * @return true if fulfilled, false otherwise
     */
    public boolean isFulfilled() {
        return fulfilled;
    }

    /**
     * Sets the fulfilled status.
     *
     * @param fulfilled the fulfilled status
     */
    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }
}
