/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.db;

import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * The Database class.
 */
public final class Database {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    
    /**
     * The connection source.
     */
    private final ConnectionSource connectionSource;

    /**
     * Instantiates a new Database.
     *
     * @param jdbcUrl the jdbc url
     * @throws SQLException the sql exception
     */
    public Database(String jdbcUrl) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(jdbcUrl);
        TableUtils.createTableIfNotExists(connectionSource, Book.class);
        TableUtils.createTableIfNotExists(connectionSource, Member.class);
        TableUtils.createTableIfNotExists(connectionSource, Loan.class);
        TableUtils.createTableIfNotExists(connectionSource, Reservation.class);
    }

    /**
     * Gets the connection source.
     *
     * @return the connection source
     */
    public ConnectionSource connectionSource() {
        return connectionSource;
    }

    /**
     * Seeds the database if empty.
     *
     * @throws SQLException the sql exception
     */
    public void seedIfEmpty() throws SQLException {
        Dao<Book, Integer> bookDao = DaoManager.createDao(connectionSource, Book.class);
        if (bookDao.queryForAll().isEmpty()) {
            log.debug("Seeding Books...");
            bookDao.create(new Book("Clean Code", "Robert C. Martin", "9780132350884", 3));
            bookDao.create(new Book("The Pragmatic Programmer", "Hunt & Thomas", "9780201616224", 2));
            bookDao.create(new Book("Design Patterns", "Gamma et al.", "9780201633610", 4));
        }

        Dao<Member, Integer> memberDao = DaoManager.createDao(connectionSource, Member.class);
        if (memberDao.queryForAll().isEmpty()) {
            log.debug("Seeding Members...");
            memberDao.create(new Member("Ada Lovelace", "ada@example.com"));
            memberDao.create(new Member("Grace Hopper", "grace@example.com"));
            memberDao.create(new Member("Alan Turing", "alan@example.com"));
        }

        Dao<Loan, Integer> loanDao = DaoManager.createDao(connectionSource, Loan.class);
        if (loanDao.queryForAll().isEmpty()) {
            log.debug("Seeding Loans...");
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            Loan returned = new Loan(members.get(0), books.get(0), today.minusDays(30), today.minusDays(9));
            returned.setReturned(true);
            returned.setReturnDate(today.minusDays(10));
            loanDao.create(returned);
            log.debug("Created returned loan.");

            Book activeBook = books.get(1);
            Loan active = new Loan(members.get(1), activeBook, today.minusDays(5), today.plusDays(16));
            loanDao.create(active);
            activeBook.setAvailableCopies(activeBook.getAvailableCopies() - 1);
            bookDao.update(activeBook);
            log.debug("Created active loan.");

            Book overdueBook = books.get(2);
            Loan overdue = new Loan(members.get(2), overdueBook, today.minusDays(30), today.minusDays(9));
            loanDao.create(overdue);
            overdueBook.setAvailableCopies(overdueBook.getAvailableCopies() - 1);
            bookDao.update(overdueBook);
            log.debug("Created overdue loan.");
        }

        Dao<Reservation, Integer> reservationDao = DaoManager.createDao(connectionSource, Reservation.class);
        if (reservationDao.queryForAll().isEmpty()) {
            log.debug("Seeding Reservations...");
            List<Book> books = bookDao.queryForAll();
            List<Member> members = memberDao.queryForAll();
            LocalDate today = LocalDate.now();

            reservationDao.create(new Reservation(members.get(0), books.get(1), today.minusDays(1)));
        }
    }
}
