package com.capella.apache.chemistry.services;

import com.capella.apache.chemistry.exceptions.DocumentNotFoundException;
import org.apache.chemistry.opencmis.client.api.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by asylum on 4/28/16.
 */
public class OpencmisServiceClientImplTest {
    OpenCmisServiceClient openCmisServiceClient = new OpencmisServiceClientImpl();
    private String documentId;

    @Before
    public void testCreateAndGetDocument()  {
        // given
        final String textFileName = "documents/helloworld.docx";
        final String fileName = "helloworld.docx";
        InputStream resourceAsStream = OpencmisServiceClientImplTest.class.getClassLoader().getResourceAsStream(textFileName);

        // when
        documentId = openCmisServiceClient.createDocument(fileName, "application/octetstream", resourceAsStream);

        // then
        assertThat(documentId, is(notNullValue()));
    }

    @Test
    public void testGetDocument() throws DocumentNotFoundException {

        // when
        Document document = openCmisServiceClient.getDocument(documentId);

        // then
        assertThat(document, is(notNullValue()));
        assertThat(document.getId(), is(documentId));
    }

    @After
    public void testDeleteDocument() {
        try {
            // when
            openCmisServiceClient.deleteDocument(documentId);

            //then
            openCmisServiceClient.getDocument(documentId);
            fail("Should not find document");
        } catch (DocumentNotFoundException e) {

        } catch (Exception e) {
            fail("unknown error occured");
        }
    }
}