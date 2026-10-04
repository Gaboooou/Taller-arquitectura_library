/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

/**
 * The LoanPolicy class.
 */
public class LoanPolicy {
    /**
     * The constant DUE_DAYS.
     */
    public static final int DUE_DAYS = 21;
    
    /**
     * The constant FEE_PER_DAY.
     */
    public static final double FEE_PER_DAY = 1.0;

    /**
     * Instantiates a new Loan policy.
     */
    private LoanPolicy() {}

    /**
     * Calculates the due date.
     *
     * @param loanDate the loan date
     * @return the due date
     */
    public static LocalDate dueDate(LocalDate loanDate){
        return loanDate.plusDays(DUE_DAYS);
    }
}
