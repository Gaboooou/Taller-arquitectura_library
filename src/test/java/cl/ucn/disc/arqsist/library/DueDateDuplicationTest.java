package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.Interface.BookDao;
import cl.ucn.disc.arqsist.library.dao.Interface.LoanDao;
import cl.ucn.disc.arqsist.library.dao.Interface.MemberDao;
import cl.ucn.disc.arqsist.library.dao.Interface.ReservationDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteBookDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteLoanDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteMemberDao;
import cl.ucn.disc.arqsist.library.dao.OrmliteReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DueDateDuplicationTest {

    private MemberService memberService;
    private ReservationService reservationService;
    private Book book;
    private Member member;

    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new OrmliteBookDao(db.connectionSource());
        MemberDao memberDao = new OrmliteMemberDao(db.connectionSource());
        LoanDao loanDao = new OrmliteLoanDao(db.connectionSource());
        ReservationDao reservationDao = new OrmliteReservationDao(db.connectionSource());

        memberService = new MemberService(memberDao, bookDao, loanDao);
        reservationService = new ReservationService(reservationDao, bookDao, memberDao, loanDao);

        book = new Book("Design Patterns", "Gamma et al.", "9780201633610", 1);
        bookDao.create(book);
        member = new Member("Grace Hopper", "grace@example.com");
        memberDao.create(member);
    }

    @Test
    void checkoutAndFulfillUseTheSameLoanPeriod() throws Exception {
        Loan fromCheckout = memberService.checkout(member.getId(), book.getId());

        Reservation reservation = reservationService.reserve(book.getId(), member.getId());
        Loan fromFulfill = reservationService.fulfill(reservation.getId());

        assertEquals(fromCheckout.getDueDate(), fromFulfill.getDueDate(),
                "the same kind of loan should have the same due date");
    }
}
