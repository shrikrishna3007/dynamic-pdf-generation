package com.project.pdf_generation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.project.pdf_generation.model.InvoiceDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class PDFService {
    @Value("${pdf.storage.path}")
    public String storagePath;

    private final CreatePDFService createPDFService;

    /*
    Method to generate hash code for json data
     */
    public String generateHash(InvoiceDetails invoiceDetails) throws NoSuchAlgorithmException, JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String jsonString=objectMapper.writeValueAsString(invoiceDetails);
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        byte[] hash=messageDigest.digest(jsonString.getBytes());
        StringBuilder hexString=new StringBuilder();
        for(byte b: hash){
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /*
    To check weather pdf already exists in local storage.
     */
    public boolean isPDFExists(String hash) {
        Path path=Paths.get(storagePath, hash + ".pdf");
        return Files.exists(path);
    }

    /*
    To generate PDF based on json data.
     */
    public byte[] generatePDF(InvoiceDetails invoiceDetails) throws NoSuchAlgorithmException, IOException, DocumentException {
        String hash=generateHash(invoiceDetails);
        String filePath= storagePath + File.separator + hash +".pdf";
        // Check weather pdf already exists
        Path path = Paths.get(filePath);
        if(isPDFExists(hash)){
            return Files.readAllBytes(path);
        }
        createPDFService.createNewPDF(invoiceDetails,filePath);
        return Files.readAllBytes(path);
    }

    /*
    To save pdf file in local storage.
     */
    public void savePDF(String hash, byte[] pdfBytes) throws IOException {
        String filePath = storagePath + File.separator + hash + ".pdf";
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }catch (Exception e) {
            throw new IOException("Failed to save PDF", e);
        }
    }

    /*
    To get pdf based on hashcode.
     */
    public byte[] getExistingPDF(String hash) throws IOException {
        Path path=Paths.get(storagePath + hash + ".pdf");
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        } else {
            throw new FileNotFoundException("PDF not found at: " + path);
        }
    }
}
