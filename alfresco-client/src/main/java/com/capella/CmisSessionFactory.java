package com.capella;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class CmisSessionFactory {
    private Session session;
    private Map parameter = new HashMap();

    public CmisSessionFactory() throws IOException {
       createSession();
    }

    private void createSession() throws IOException {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
        parameter.put(SessionParameter.PASSWORD, "admin");
        parameter.put(SessionParameter.ATOMPUB_URL, "http://localhost:9009/alfresco/api/-default-/public/cmis/versions/1.1/atom");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.REPOSITORY_ID, "-default-");
        session = sessionFactory.createSession(parameter);
    }

    public List<Repository> listRepositories(Map parameter){
        return SessionFactoryImpl.newInstance().getRepositories(parameter);
    }

    public Folder findFolder(String folderName){
        ItemIterable<CmisObject> children = session.getRootFolder().getChildren();
        System.out.println("Found the following objects in the root folder:-");
        for (CmisObject o : children) {
            if(o instanceof Folder && o.getName().equals(folderName)){
               return (Folder) o;
            }

        }
        return null;
    }

    public Folder createFolder(String folderName){
        Map<String, String> newFolderProps = new HashMap<String, String>();
        newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");

        newFolderProps.put(PropertyIds.NAME, folderName);

        return session.getRootFolder().createFolder(newFolderProps);
    }

    public void readDocument(Document document) throws IOException {
        ContentStream contentStream = document.getContentStream(); // returns null if the document has no content
        if (contentStream != null) {
            String content = getContentAsString(contentStream);
            System.out.println("Contents of " + document.getName() + " are: " + content);
        } else {
            System.out.println("No content.");
        }
    }

    public  Document getDocument(String documentId) {
        ObjectId objectId = session.createObjectId(documentId);
        Document document = (Document) session.getObject(objectId);
        System.out.println("o =" + document.getName());
        return document;
    }


    public String createDocument(Folder folder) throws IOException {
        final String textFileName = "helloworld.docx";
        System.out.println("creating a simple text file, " + textFileName);

        String mimetype = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        String filename = textFileName;

        InputStream resourceAsStream = CmisSessionFactory.class.getClassLoader().getResourceAsStream(textFileName);
        byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(resourceAsStream);

        ContentStream contentStream = session.getObjectFactory().createContentStream(filename, bytes.length, mimetype, new ByteArrayInputStream(bytes));


        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, filename);

        Document doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);

        System.out.println("Document ID: " + doc.getId());

        return doc.getId();
    }
    /**
     * Helper method to get the contents of a stream
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private static String getContentAsString(ContentStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");

        try {
            final char[] buffer = new char[4 * 1024];
            int b;
            while (true) {
                b = reader.read(buffer, 0, buffer.length);
                if (b > 0) {
                    sb.append(buffer, 0, b);
                } else if (b == -1) {
                    break;
                }
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }


    public static void main(String[] args) throws IOException {
        CmisSessionFactory factory = new CmisSessionFactory();

        Folder folderName = factory.findFolder("ADGNewFolder");
        String documentId = factory.createDocument(folderName);

        Document document = factory.getDocument(documentId);

        System.out.println("Document Id :" + document.getId());
        System.out.println("Document Name :" + document.getName());
        /*Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

       properties.put("User_Custom", "Ramesh");

        document.updateProperties(properties, true);*/
    }
}
