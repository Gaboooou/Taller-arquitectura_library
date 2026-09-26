package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.CrudDao;
import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public final class OrmliteBookDao extends BaseDao<Book> implements BookDao {
    public OrmliteBookDao(ConnectionSource connectionSource) {
        super(connectionSource, Book.class);
    }
}
