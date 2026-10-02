package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

public class LoanPolicy {

    public LoanPolicy() {}
    static int DUE_DAYS = 21;
    static double FEE_PER_DAY = 1.0;

    public static Object dueDate(LocalDate loanDate){return loanDate.plusDays(DUE_DAYS);}
}
