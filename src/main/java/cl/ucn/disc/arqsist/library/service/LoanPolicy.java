package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

public class LoanPolicy {
    public static final int DUE_DAYS = 21;
    public static final double FEE_PER_DAY = 1.0;

    private LoanPolicy() {}

    public static LocalDate dueDate(LocalDate loanDate){
        return loanDate.plusDays(DUE_DAYS);
    }
}
