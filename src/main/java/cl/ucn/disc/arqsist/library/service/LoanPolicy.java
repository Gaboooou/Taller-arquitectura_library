/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;


/**
 * Centralizes the lending policy: loan period and overdue fee.
 */
public class LoanPolicy {

    public LoanPolicy() {}
    /** Number of days a loan lasts, counted from the loan date. */
    static int DUE_DAYS = 21;
    /** Number of fee per day */
    static double FEE_PER_DAY = 1.0;

    public static Object dueDate(LocalDate loanDate){return loanDate.plusDays(DUE_DAYS);}
}
