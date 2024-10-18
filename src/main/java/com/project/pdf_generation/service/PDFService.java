package com.project.pdf_generation.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.pdf_generation.model.InvoiceDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PDFService {
    @Value("${pdf.storage.path}")
    private String storagePath;

    public byte[] generatePDF(InvoiceDetails invoiceDetails) throws IOException, DocumentException, NoSuchAlgorithmException {
        String hash=generateHash(invoiceDetails);
        String filePath= storagePath + File.separator + hash +".pdf";
        // Check weather pdf already exists
        File file=new File(filePath);
        File pdfDirectory = new File(storagePath);
        if (!pdfDirectory.exists()) {
            pdfDirectory.mkdirs();
        }
        Path path = Paths.get(filePath);
        if(file.exists()){
            return Files.readAllBytes(path);
        }
        createNewPDF(invoiceDetails,filePath);
        return Files.readAllBytes(path);
    }

    private void createNewPDF(InvoiceDetails invoiceDetails, String filePath) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(filePath));
        document.open();
        document.addTitle("Invoice Details.");
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        PdfPCell cell1 = new PdfPCell();
        cell1.setRowspan(6);
        cell1.setColspan(2);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.add(new Paragraph("Seller: "));
        paragraph1.add(new Paragraph(invoiceDetails.getSeller()));
        paragraph1.add(new Paragraph("GSTIN : "+invoiceDetails.getSellerGSTIN()));
        paragraph1.add(new Paragraph("Address: "+invoiceDetails.getSellerAddress()));
        cell1.setPhrase(paragraph1);
        table.addCell(cell1);

        PdfPCell cell2=new PdfPCell();
        cell2.setRowspan(6);
        cell2.setColspan(2);
        Paragraph paragraph2 = new Paragraph();
        paragraph2.add(new Paragraph("Buyer: "));
        paragraph2.add(new Paragraph(invoiceDetails.getBuyer()));
        paragraph2.add(new Paragraph("GSTIN : "+invoiceDetails.getBuyerGSTIN()));
        paragraph2.add(new Paragraph("Address: "+invoiceDetails.getBuyerAddress()));
        cell2.setPhrase(paragraph2);
        table.addCell(cell2);

        table.addCell("Item");
        table.addCell("Quantity");
        table.addCell("Rate");
        table.addCell("Amount");

        table.addCell(invoiceDetails.getItems().);
        document.add(table);
        document.close();
    }

    private String generateHash(InvoiceDetails invoiceDetails) throws NoSuchAlgorithmException {
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        byte[] hash=messageDigest.digest(invoiceDetails.toString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
