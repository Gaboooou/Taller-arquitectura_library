/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.model.Book;

import java.util.List;

/**
 * The BookService class.
 */
public final class BookService {

    /**
     * The dao.
     */
    private final BookDao dao;

    /**
     * Instantiates a new Book service.
     *
     * @param dao the dao
     */
    public BookService(BookDao dao) {
        this.dao = dao;
    }

    /**
     * Lists all books.
     *
     * @return the list of books
     */
    public List<Book> listAll() {
        return dao.findAll();
    }

    /**
     * Finds a book by id.
     *
     * @param id the id
     * @return the book
     */
    public Book findById(int id) {
        return dao.findById(id);
    }

    /**
     * Creates a new book.
     *
     * @param book the book
     * @return the created book
     */
    public Book create(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        dao.create(book);
        return book;
    }

    /**
     * Borrows a book.
     *
     * @param bookId the book id
     */
    public void borrow(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies of book " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        dao.update(book);
    }

    /**
     * Returns a copy of a book.
     *
     * @param bookId the book id
     */
    public void returnCopy(int bookId) {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        dao.update(book);
    }
}
