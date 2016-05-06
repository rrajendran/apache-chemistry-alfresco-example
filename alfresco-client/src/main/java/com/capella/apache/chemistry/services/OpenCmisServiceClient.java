package com.capella.apache.chemistry.services;

import com.capella.apache.chemistry.exceptions.DocumentManagementException;
import com.capella.apache.chemistry.exceptions.DocumentNotFoundException;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Ramesh Rajendran   on 4/22/16.
 */
public interface OpenCmisServiceClient {

    /**
     * Create document
     *
     * @param fileName Name of the file
     * @param mimeType  Mimetype of the document
     * @param inputStream {@link InputStream} of the document
     * @return Return documentId
     * @throws IOException
     */
    String createDocument(String fileName, String mimeType, InputStream inputStream) throws DocumentNotFoundException;

    String updateDocument(String documentId, InputStream inputStream) throws DocumentManagementException;

    Document findDocumentByFileName(String name) throws DocumentNotFoundException;

    /**
     * Find folder
     *
     * @param folderName Name of the folder
     * @return Returns {@link Folder}
     */
    Folder findFolder(String folderName);

    /**
     * Create folder
     *
     * @param folderName Name of the folder
     * @return Returns {@link Folder}
     */
    Folder createFolder(String folderName);

    /**
     * Get document
     *
     * @param documentId Document id
     * @return Returns {@link Document}
     */
    Document getDocument(String documentId) throws DocumentNotFoundException;

    /**
     * Delete document
     *
     * @param documentId document id
     */
    void deleteDocument(String documentId) throws DocumentNotFoundException;

    /**
     * Get metadata
     *
     * @param documentId document id
     * @return Returns Map of PROPERTIES for the document
     * @throws DocumentNotFoundException
     */
    Map<String, String> getMetadata(String documentId) throws DocumentNotFoundException;
    /**
     * Update document meta data.
     *
     * @param metadata
     * @param documentId
     * @return
     */
    void updateMetadata(Map<String, String> metadata, String documentId) throws DocumentNotFoundException;
}
