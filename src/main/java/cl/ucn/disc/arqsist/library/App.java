/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
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
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;

/**
 * The App class.
 */
public final class App {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(App.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private App() {
    }

    /**
     * The main entry point of the application.
     *
     * @param args the command line arguments
     * @throws Exception if an initialization error occurs
     */
    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:database.db");
        db.seedIfEmpty();

        Clock clock = Clock.system(ZoneId.of("America/Santiago"));

        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        ReservationDao reservationDao = new OrmliteReservationDao(db.connectionSource());

        BookService bookService = new BookService(bookDao);
        MemberService memberService = new MemberService(memberDao);
        LoanService loanService = new LoanService(loanDao, memberDao, bookService, clock);
        ReservationService reservationService = new ReservationService(reservationDao, memberDao, loanDao, bookService, clock);

        log.info("Services initialized.");

        BookController bookController = new BookController(bookService);
        MemberController memberController = new MemberController(memberService);
        LoanController loanController = new LoanController(loanService);
        ReservationController reservationController = new ReservationController(reservationService);

        log.info("Controllers initialized.");

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });

            config.jsonMapper(new JavalinJackson().updateMapper(mapper ->
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            ));

            config.routes.exception(NotFoundException.class, (e, ctx) ->
                    ctx.status(404).json(Map.of("error", e.getMessage()))
            );

            config.routes.exception(IllegalArgumentException.class, (e, ctx) ->
                    ctx.status(400).json(Map.of("error", e.getMessage()))
            );

            config.routes.exception(IllegalStateException.class, (e, ctx) ->
                    ctx.status(409).json(Map.of("error", e.getMessage()))
            );
            bookController.register(config);
            memberController.register(config);
            loanController.register(config);
            reservationController.register(config);
        });

        app.start(7070);
        log.info("Server started on port 7070");
    }
}
