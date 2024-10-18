package com.project.pdf_generation.controller;

import com.itextpdf.text.DocumentException;
import com.project.pdf_generation.model.InvoiceDetails;
import com.project.pdf_generation.service.PDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PDFController {
    private final PDFService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePDF(@RequestBody InvoiceDetails invoiceDetails){
        try{
            byte[] pdfBytes= pdfService.generatePDF(invoiceDetails);
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.add("Content-Type", "application/pdf");
            return ResponseEntity.ok().headers(httpHeaders)
                    .body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
