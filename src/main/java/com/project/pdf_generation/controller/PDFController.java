package com.project.pdf_generation.controller;

import com.itextpdf.text.DocumentException;
import com.project.pdf_generation.model.InvoiceDetails;
import com.project.pdf_generation.service.PDFService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PDFController {
    private final PDFService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<String> generatePDF(@RequestBody InvoiceDetails invoiceDetails) throws NoSuchAlgorithmException, DocumentException, IOException {
        String hash=pdfService.generateHash(invoiceDetails);
        if (pdfService.isPDFExists(hash)){
            HttpHeaders headers=new HttpHeaders();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .headers(headers)
                    .body("PDF Exists..."+hash);
        }
        byte[] pdfBytes=pdfService.generatePDF(invoiceDetails);
        pdfService.savePDF(hash,pdfBytes);
        return ResponseEntity.status(HttpStatus.OK).body("PDF Generated..."+hash);
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable String hash) throws IOException {
        byte[] pdfBytes = pdfService.getExistingPDF(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
