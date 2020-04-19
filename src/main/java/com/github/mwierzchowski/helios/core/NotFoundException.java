package com.github.mwierzchowski.helios.core;

import lombok.Getter;

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
    public final static String template = "{0} with id {1} was not found";

    private final Class<?> clazz;
    private final Object id;
    /**
     * Constructor
     * @param clazz class of missing entity
     * @param id id of missing entity
     */
    public NotFoundException(Class<?> clazz, Object id) {
        super(format(template, clazz.getSimpleName(), id));
        this.clazz = clazz;
        this.id = id;
    }
}
