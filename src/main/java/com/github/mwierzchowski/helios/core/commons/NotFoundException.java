package com.github.mwierzchowski.helios.core.commons;

import lombok.Getter;

import java.io.Serializable;

import static java.text.MessageFormat.format;

/**
 * Exception for cases when domain entity is missing.
 * @author Marcin Wierzchowski
 */
@Getter
public class NotFoundException extends RuntimeException {
    /**
     * Template of the message
     */
    public final static String TEMPLATE = "{0} with id {1} was not found";

    /**
     * Class of missing entity
     */
    private final Class<?> clazz;

    /**
     * Id of missing entity
     */
    private final Serializable id;
    /**
     * Constructor
     * @param clazz class of missing entity
     * @param id id of missing entity
     */
    public NotFoundException(Class<?> clazz, Serializable id) {
        super(format(TEMPLATE, clazz.getSimpleName(), id));
        this.clazz = clazz;
        this.id = id;
    }
}
