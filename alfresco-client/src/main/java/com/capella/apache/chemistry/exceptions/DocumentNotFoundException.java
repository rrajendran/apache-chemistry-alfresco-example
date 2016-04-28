package com.capella.apache.chemistry.exceptions;

/**
 * Document not found exception
 * Created by Ramesh Rajendran   on 4/24/16.
 */
public class DocumentNotFoundException extends Throwable {

	private static final long serialVersionUID = -5639041131698078520L;

	/**
     * Constructor
     *
     * @param exception Exception message
     * @param throwable {@link Throwable} type
     */
    public DocumentNotFoundException(String exception, Throwable throwable) {
        super(exception, throwable);
    }
}
