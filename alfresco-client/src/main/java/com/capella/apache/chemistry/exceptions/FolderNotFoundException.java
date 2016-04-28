package com.capella.apache.chemistry.exceptions;

/**
 * Folder not found exception
 * Created by Ramesh Rajendran   on 4/24/16.
 */
public class FolderNotFoundException extends RuntimeException {
    /**
     * Constructor
     *
     * @param e {@link Throwable} type
     */
    public FolderNotFoundException(Throwable e) {
        super(e);
    }
}
