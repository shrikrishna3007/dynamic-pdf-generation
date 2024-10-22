package com.project.pdf_generation.unit_test.controller_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;
import com.project.pdf_generation.controller.PDFController;
import com.project.pdf_generation.model.InvoiceDetails;
import com.project.pdf_generation.model.Items;
import com.project.pdf_generation.service.PDFService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PDFControllerTest {
    @Mock
    private PDFService pdfService;
    @InjectMocks
    private PDFController pdfController;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        InvoiceDetails invoiceDetails=new InvoiceDetails();
        invoiceDetails.setSeller("Seller");
        invoiceDetails.setSellerGSTIN("test11");
        invoiceDetails.setSellerAddress("Address");
        invoiceDetails.setBuyer("Buyer");
        invoiceDetails.setBuyerGSTIN("test22");
        invoiceDetails.setBuyerAddress("Address");

        List<Items> itemsList=new ArrayList<>();
        Items item1=new Items();
        item1.setName("Item1");
        item1.setQuantity("10");
        item1.setRate(100.0);
        item1.setAmount(1000.0);

        invoiceDetails.setItems(itemsList);
    }

    @Test
    void generatePDF_AlreadyExistedFileTest() throws NoSuchAlgorithmException, IOException, DocumentException {
        InvoiceDetails invoiceDetails=new InvoiceDetails();
        String hash="test_hash";

        // call service methods
        when(pdfService.generateHash(invoiceDetails)).thenReturn(hash);
        when(pdfService.isPDFExists(hash)).thenReturn(true);

        // act
        ResponseEntity<String> response=pdfController.generatePDF(invoiceDetails);

        //assertions
        assertEquals(HttpStatus.FOUND,response.getStatusCode());
        assertEquals("PDF Exists. Here is the File Name: "+hash,response.getBody());
        // verify that service methods were called
        verify(pdfService,times(1)).generateHash(invoiceDetails);
        verify(pdfService,times(1)).isPDFExists(hash);
        verify(pdfService,never()).generatePDF(invoiceDetails);
        verify(pdfService,never()).savePDF(any(),any());
    }

    @Test
    void generatePDF_WhenPdfDoesNotExist() throws NoSuchAlgorithmException, IOException, DocumentException {
        InvoiceDetails invoiceDetails=new InvoiceDetails();
        String hash="test_hash";
        byte[] pdfBytes=new byte[0];

        // call service methods
        when(pdfService.generateHash(invoiceDetails)).thenReturn(hash);
        when(pdfService.isPDFExists(hash)).thenReturn(false);
        when(pdfService.generatePDF(invoiceDetails)).thenReturn(pdfBytes);

        //act
        ResponseEntity<String> response=pdfController.generatePDF(invoiceDetails);
        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PDF Generated. Here is the File name: "+hash,response.getBody());
        // verify that service methods were called
        verify(pdfService, times(1)).generateHash(invoiceDetails);
        verify(pdfService, times(1)).isPDFExists(hash);
        verify(pdfService, times(1)).generatePDF(invoiceDetails);
        verify(pdfService,times(1)).savePDF(hash,pdfBytes);
    }

    @Test
    void generatePDF_NoSuchAlgorithmExceptionTest() throws NoSuchAlgorithmException, JsonProcessingException {
        InvoiceDetails invoiceDetails=new InvoiceDetails();
        // call service methods
        when(pdfService.generateHash(invoiceDetails)).thenThrow(NoSuchAlgorithmException.class);

        assertThrows(NoSuchAlgorithmException.class,()-> pdfController.generatePDF(invoiceDetails));
    }

    @Test
    void generatePDF_IOExceptionTest() throws NoSuchAlgorithmException, IOException, DocumentException {
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        String hash = "dummyHash";
        byte[] pdfBytes = new byte[0];

        when(pdfService.generateHash(invoiceDetails)).thenReturn(hash);
        when(pdfService.generatePDF(invoiceDetails)).thenReturn(pdfBytes);
        doThrow(new IOException("File saving error")).when(pdfService).savePDF(hash,pdfBytes);

        // Act & Assert
        assertThrows(IOException.class, () -> pdfController.generatePDF(invoiceDetails));
    }

    @Test
    void generatePDF_DocumentExceptionTest() throws NoSuchAlgorithmException, IOException, DocumentException {
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        String hash = "dummyHash";
        byte[] pdfBytes = new byte[0];

        when(pdfService.generateHash(invoiceDetails)).thenReturn(hash);
        when(pdfService.generatePDF(invoiceDetails)).thenReturn(pdfBytes);
        doThrow(new DocumentException("PDF Generation error")).when(pdfService).generatePDF(invoiceDetails);

        // Act & Assert
        assertThrows(DocumentException.class, () -> pdfController.generatePDF(invoiceDetails));
    }

    @Test
    void downloadPDFTest() throws IOException, NoSuchAlgorithmException {
        String hash="test_hash";
        byte[] pdfBytes=new byte[0];
        // call service methods
        when(pdfService.getExistingPDF(hash)).thenReturn(pdfBytes);
        //call controller method.
        ResponseEntity<byte[]> response=pdfController.downloadPDF(hash);

        // assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pdfBytes,response.getBody());
        // verify that service methods were called or not.
        verify(pdfService, times(1)).getExistingPDF(hash);
        verify(pdfService, never()).generateHash(any());
    }

    @Test
    void downloadPDF_IOExceptionTest() throws IOException {
        String hash="test_hash";
        // call service methods
        doThrow(new IOException("File reading error")).when(pdfService).getExistingPDF(hash);
        assertThrows(IOException.class, () -> pdfController.downloadPDF(hash));
    }
}
