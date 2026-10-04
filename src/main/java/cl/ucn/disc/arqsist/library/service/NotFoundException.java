/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

/**
 * The NotFoundException class.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Creates a new exception with the given message.
     *
     * @param message a description of the missing record.
     */
    public NotFoundException(String message){
        super(message);
    }
}
