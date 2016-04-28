package com.capella.apache.chemistry.exceptions;

/**
 * Document management exception
 * Created by Ramesh Rajendran   on 4/24/16.
 */
public class DocumentManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @param exception Exception message
     */
    public DocumentManagementException(String exception) {
        super(exception);
    }

    /**
     * Constructor
     * @param exception Exception message
     * @param throwable {@link Throwable}
     */
    public DocumentManagementException(String exception, Throwable throwable) {
        super(exception, throwable);
    }
}
