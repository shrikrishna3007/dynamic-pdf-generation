package com.project.pdf_generation.unit_test.service_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.pdf_generation.model.InvoiceDetails;
import com.project.pdf_generation.model.Items;
import com.project.pdf_generation.service.CreatePDFService;
import com.project.pdf_generation.service.PDFService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PDFServiceTest {
    @Mock
    private CreatePDFService createPDFService;

    @InjectMocks
    private PDFService pdfService;

    private static final String STORAGE_PATH = "src/main/resources/pdfs/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Ensure mocks are initialized
        pdfService = spy(new PDFService(createPDFService));
        pdfService.storagePath = STORAGE_PATH; // Initialize storagePath directly
    }

    @AfterEach
    void tearDown(){
        Mockito.clearAllCaches();
    }

    @Test
    void generateHashTest() throws NoSuchAlgorithmException, JsonProcessingException {
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        // Act
        String hash = pdfService.generateHash(invoiceDetails);
        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertEquals(64, hash.length());
    }

    @Test
    void isPDFExists_PDFExistTest() {
        // Arrange
        String hash = "test_hash";
        Path path = Paths.get(STORAGE_PATH, hash + ".pdf");
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(path)).thenReturn(true);

            // Act
            boolean exists = pdfService.isPDFExists(hash);

            // Assert
            assertTrue(exists);
        }
    }

    @Test
    void generatePDF_ExistingPDF_Test() throws Exception {
        // Arrange
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        String hash = "test_hash";
        String filePath = STORAGE_PATH + hash + ".pdf";  // Construct file path
        Path pdfPath = Paths.get(filePath);
        byte[] pdfBytes = new byte[0];  // Sample PDF byte array

        // Mocking the behavior
        doReturn(hash).when(pdfService).generateHash(invoiceDetails);
        doReturn(true).when(pdfService).isPDFExists(hash);

        // Mock static methods in Files class
        MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
        mockedFiles.when(() -> Files.exists(pdfPath)).thenReturn(true);
        mockedFiles.when(() -> Files.readAllBytes(pdfPath)).thenReturn(pdfBytes);

        // Act
        byte[] result = pdfService.generatePDF(invoiceDetails);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertArrayEquals(pdfBytes, result, "The returned PDF bytes should match the mocked value");
        verify(createPDFService, never()).createNewPDF(any(), any());
    }

    @Test
    void generatePDF_CreatesNewPDF_Test() throws Exception {
        // Arrange
        InvoiceDetails invoiceDetails = getInvoiceDetails();

        String hash = "test_hash";
        String filePath = STORAGE_PATH + File.separator + hash + ".pdf";
        byte[] pdfBytes = new byte[]{1, 2, 3}; // Sample PDF byte array to simulate the created PDF

        // Stubbing methods
        doReturn(hash).when(pdfService).generateHash(invoiceDetails);
        doReturn(false).when(pdfService).isPDFExists(hash); // Simulate that the PDF doesn't exist

        // Mock the createNewPDF method to simulate PDF creation
        doNothing().when(createPDFService).createNewPDF(any(), anyString());

        // Mock static methods for reading files
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(Paths.get(filePath))).thenReturn(pdfBytes); // Simulate reading the generated PDF

            // Act
            byte[] result = pdfService.generatePDF(invoiceDetails); // Call the method

            // Assert
            assertNotNull(result); // Ensure the result is not null
            assertArrayEquals(pdfBytes, result); // Verify the returned PDF bytes match the mocked bytes
            verify(createPDFService, times(1)).createNewPDF(invoiceDetails,filePath); // Verify that createNewPDF was called
        }
    }

    private static InvoiceDetails getInvoiceDetails() {
        InvoiceDetails invoiceDetails = new InvoiceDetails();
        invoiceDetails.setSeller("Test Seller");
        invoiceDetails.setSellerGSTIN("123456789");
        invoiceDetails.setSellerAddress("123 Seller St, City, State, Zip");
        invoiceDetails.setBuyer("Test Buyer");
        invoiceDetails.setBuyerGSTIN("987654321");
        invoiceDetails.setBuyerAddress("456 Buyer St, City, State, Zip");

        // Create a list of Items
        List<Items> itemsList = new ArrayList<>();
        Items item1 = new Items();
        item1.setName("Test Item");
        item1.setQuantity("10");
        item1.setRate(100.0);
        item1.setAmount(1000.0);
        itemsList.add(item1);
        invoiceDetails.setItems(itemsList);
        return invoiceDetails;
    }


    @Test
    void getExistingPDF_PDFExistsTest() throws IOException {
    // Arrange
        String hash = "test_hash";
        Path path = Paths.get(STORAGE_PATH + hash + ".pdf");
        byte[] pdfData = new byte[]{1, 2, 3}; // Sample PDF byte array

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(path)).thenReturn(true);
            mockedFiles.when(() -> Files.readAllBytes(path)).thenReturn(pdfData);

            // Act
            byte[] result = pdfService.getExistingPDF(hash);

            // Assert
            assertNotNull(result);
            assertArrayEquals(pdfData, result);
        }
    }

    @Test
    void getExistingPDF_PDFDoesNotExist_Test() {
        // Arrange
        String hash = "test_hash";
        Path path = Paths.get(STORAGE_PATH + hash + ".pdf");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(path)).thenReturn(false);

            // Act & Assert
            FileNotFoundException thrown = assertThrows(FileNotFoundException.class, () -> pdfService.getExistingPDF(hash));
            assertEquals("PDF not found at: " + path, thrown.getMessage());
        }
    }
}
