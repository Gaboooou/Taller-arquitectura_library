package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.dao.Interface.CrudDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public final class OrmliteLoanDao extends BaseDao<Loan> implements LoanDao {
    public OrmliteLoanDao(ConnectionSource connectionSource) {
        super(connectionSource, Loan.class);
    }

}
