package com.capella.apache.chemistry.services;

import com.capella.apache.chemistry.exceptions.DocumentManagementException;
import com.capella.apache.chemistry.exceptions.DocumentNotFoundException;
import com.capella.apache.chemistry.exceptions.DocumentUpdateException;
import com.capella.apache.chemistry.mappers.PropertyIdMapper;
import org.apache.chemistry.opencmis.client.SessionParameterMap;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Opencmis Service Client
 */
public class OpencmisServiceClientImpl implements OpenCmisServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpencmisServiceClientImpl.class);
    public static final String FOLDER_NAME = "IPT_DOCS";
    public static final String OBJECT_MODEL = "D:ipt:customDocumentModel";
    //public static final String OBJECT_MODEL = "cmis:document";
    private static final String FOLDER_ID = "cmis:folder";
    private static Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(uncheck(()->OpencmisServiceClientImpl.class.getClassLoader().getResourceAsStream("alfresco.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createDocument(String fileName, String mimeType, InputStream inputStream) throws DocumentNotFoundException {

        checkIfNull(fileName, "File name is required");
        checkIfNull(mimeType, "Mime type is required");
        checkIfNull(inputStream, "Inputstream cannot be null");
        try {

            Document findDoc = findDocumentByFileName(fileName);
            if (findDoc != null) {
                updateDocument(findDoc.getId(), inputStream);
                return findDoc.getId();
            } else {
                byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(inputStream);

                final Session session = getSession(null);
                ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, bytes.length, mimeType, new ByteArrayInputStream(bytes));

                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PropertyIds.OBJECT_TYPE_ID, OBJECT_MODEL);
                properties.put(PropertyIds.NAME, fileName);
                properties.put("ipt:sourceSystem", "test");

                Folder folder = findFolder(FOLDER_NAME);
                if (folder == null) {
                    folder = createFolder(FOLDER_NAME);
                }

                Document doc = folder.createDocument(properties, contentStream, VersioningState.NONE);
                return doc.getId();
            }
        } catch (Exception e) {
            throw new DocumentManagementException("Error creating document - " + e.getMessage(), e);
        }
    }

    @Override
    public void updateDocument(String documentId, InputStream inputStream) throws DocumentManagementException {
        try {
            Session session = getSession(null);
            Document document = (Document) session.getObject(documentId);

            byte[] bytes = IOUtils.toByteArray(inputStream);
            ContentStream contentStream = session.getObjectFactory().createContentStream(document.getContentStreamFileName(),
                    bytes.length, document.getContentStreamMimeType(),
                    new ByteArrayInputStream(bytes));

            if (session.getRepositoryInfo().getCapabilities().getContentStreamUpdatesCapability()
                    .equals(CapabilityContentStreamUpdates.ANYTIME)) {
                LOGGER.info("Repository support anytime update");
                document.setContentStream(contentStream, true);
            } else {
                throw new DocumentUpdateException(String.format("Updates not allowed on document '%s'", documentId));
            }

        } catch (Exception ex) {
            throw new DocumentManagementException(String.format("Error creating document '%s'", documentId), ex);
        }
    }

    /**
     * Search documents
     *
     * @param name
     * @return
     */
    @Override
    public Document findDocumentByFileName(String name) throws DocumentNotFoundException {
        Session session = getSession(null);
        ItemIterable<QueryResult> results = session.query("SELECT * FROM cmis:document where cmis:name = '" + name + "'", false);
        if (results.getTotalNumItems() > 1) {
            throw new DocumentManagementException("Multiple results returned");
        }

        for (QueryResult hit : results) {
            PropertyData<Object> propertyById = hit.getPropertyById("cmis:objectId");
            return (Document) session.getObject(propertyById.getFirstValue().toString());
        }
        return null;
    }
    @Override
    public Folder findFolder(String folderName) {
        checkIfNull(folderName, "folder name is required");

        try {
            final Session session = getSession(null);
            ItemIterable<CmisObject> children = session.getRootFolder().getChildren();

            for (CmisObject o : children) {
                if (o instanceof Folder && o.getName().equals(folderName)) {
                    return (Folder) o;
                }
            }
        } catch (Exception e) {
            throw new DocumentManagementException("Cannot find folder - " + folderName, e);
        }
        return null;
    }

    @Override
    public Folder createFolder(String folderName) {
        checkIfNull(folderName, "folder name is required");

        Map<String, String> properties = new HashMap();
        properties.put(PropertyIds.OBJECT_TYPE_ID, FOLDER_ID);
        properties.put(PropertyIds.NAME, folderName);
        return getSession(null).getRootFolder().createFolder(properties);
    }

    @Override
    public Document getDocument(String documentId) throws DocumentNotFoundException {
        checkIfNull(documentId, "document id is required");

        try {
            final Session session = getSession(null);
            return (Document) session.getObject(documentId);
        } catch (CmisObjectNotFoundException e) {
            throw new DocumentNotFoundException("Document not found - " + e.getMessage(), e);
        } catch (Exception e) {
            throw new DocumentManagementException("Unknown error - " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDocument(String documentId) throws DocumentNotFoundException {
        CmisObject object = getDocument(documentId);
        object.delete(true);
    }

    /**
     * Get metadata
     *
     * @param documentId document id
     * @return Returns Map of PROPERTIES for the document
     * @throws DocumentNotFoundException
     */
    @Override
    public Map<String, String> getMetadata(String documentId) throws DocumentNotFoundException {

        checkIfNull(documentId, "document id cannot be null");

        final Document document = getDocument(documentId);
        Map<String, String> map = new TreeMap<>();
        List<Property<?>> documentProperties = document.getProperties();

        Iterator<Property<?>> propertiesIterator = documentProperties.iterator();
        while (propertiesIterator.hasNext()) {
            Property<?> property = propertiesIterator.next();
            map.put(property.getLocalName(), property.getValueAsString());
        }
        return map;
    }

    @Override
    public void updateMetadata(Map<String, String> metadata, String documentId) throws DocumentNotFoundException {
        checkIfNull(documentId, "Document id cannot be null");
        if (metadata == null || metadata.size() == 0) {
           throw new IllegalArgumentException("document id missing");
        }
        final Document document = getDocument(documentId);


        Map<String, String> mappedObjectProperties = PropertyIdMapper.map(metadata);
        document.updateProperties(mappedObjectProperties, true);
    }

    private void checkIfNull(Object paramName, String message) {
        if (Objects.isNull(paramName)) {
            throw new IllegalArgumentException(message);
        }
    }

    private Session getSession(Map<String, String> headers) {
        SessionParameterMap sessionParameterMap = new SessionParameterMap((Map) PROPERTIES);
        if (headers != null && headers.size() > 0) {
            headers.entrySet().stream().forEach(entry -> sessionParameterMap.addHeader(entry.getKey(), entry.getValue()));
        }
        return SessionFactoryImpl.newInstance().createSession(sessionParameterMap);
    }

    private static <T> T uncheck(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }
}
