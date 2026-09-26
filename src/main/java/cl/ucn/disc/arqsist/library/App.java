package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.controller.BookController;
import cl.ucn.disc.arqsist.library.controller.LoanController;
import cl.ucn.disc.arqsist.library.controller.MemberController;
import cl.ucn.disc.arqsist.library.controller.ReservationController;
import cl.ucn.disc.arqsist.library.dao.*;
import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public final class App {

    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:database.sqlite");
        db.seedIfEmpty();

        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        ReservationDao reservationDao = new OrmliteReservationDao(db.connectionSource());

        BookService bookService = new BookService(bookDao);
        MemberService memberService = new MemberService(memberDao, bookDao, loanDao);
        LoanService loanService = new LoanService(loanDao, bookDao);
        ReservationService reservationService = new ReservationService(reservationDao, bookDao, memberDao, loanDao);

        BookController bookController = new BookController(bookService);
        MemberController memberController = new MemberController(memberService);
        LoanController loanController = new LoanController(memberService, loanService);
        ReservationController reservationController = new ReservationController(reservationService);

        Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            bookController.register(config);
            memberController.register(config);
            loanController.register(config);
            reservationController.register(config);
        }).start(7070);
    }
}
