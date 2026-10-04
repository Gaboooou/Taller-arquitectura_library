
/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

/**
 * Creates a new exception with the given message.
 *
 * @param message a description of the missing record.
 */
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){super(message);}
}
