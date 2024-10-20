package com.project.pdf_generation.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.pdf_generation.model.InvoiceDetails;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Service
public class CreatePDFService {
    void createNewPDF(InvoiceDetails invoiceDetails, String filePath) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(filePath));
        document.open();
        document.addTitle("Invoice Details.");
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setPaddingTop(10);

        PdfPCell cell1 = new PdfPCell();
        cell1.setRowspan(6);
        cell1.setColspan(2);
        cell1.setPadding(10);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.add("Seller: \n");
        paragraph1.add("Seller: "+invoiceDetails.getSeller()+"\n");
        paragraph1.add("SellerGSTIN: "+invoiceDetails.getSellerGSTIN()+"\n");
        paragraph1.add("Seller Address: "+invoiceDetails.getSellerAddress()+"\n");
        cell1.setPhrase(paragraph1);
        table.addCell(cell1);

        PdfPCell cell2=new PdfPCell();
        cell2.setRowspan(6);
        cell2.setColspan(2);
        cell2.setPadding(10);
        Paragraph paragraph2 = new Paragraph();
        paragraph2.add("Buyer: \n");
        paragraph2.add("Buyer"+invoiceDetails.getBuyer()+"\n");
        paragraph2.add("BuyerGSTIN: "+invoiceDetails.getBuyerGSTIN()+"\n");
        paragraph2.add("Buyer Address: "+invoiceDetails.getBuyerAddress()+"\n");
        cell2.setPhrase(paragraph2);
        table.addCell(cell2);

        PdfPCell itemName=new PdfPCell(new Paragraph("Item"));
        itemName.setPadding(10f);
        table.addCell(itemName);

        PdfPCell itemQuantity=new PdfPCell(new Paragraph("Quantity"));
        itemQuantity.setPadding(10f);
        table.addCell(itemQuantity);

        PdfPCell itemRate=new PdfPCell(new Paragraph("Rate"));
        itemRate.setPadding(10f);
        table.addCell(itemRate);

        PdfPCell itemAmount=new PdfPCell(new Paragraph("Amount"));
        itemAmount.setPadding(10f);
        table.addCell(itemAmount);

        for(InvoiceDetails.Items item: invoiceDetails.getItems()){
            PdfPCell itemNameCell=new PdfPCell(new Paragraph(item.getName()));
            itemNameCell.setPadding(10f);
            table.addCell(itemNameCell);

            PdfPCell itemQuantityCell=new PdfPCell(new Paragraph(item.getQuantity()));
            itemQuantityCell.setPadding(10f);
            table.addCell(itemQuantityCell);

            PdfPCell itemRateCell=new PdfPCell(new Paragraph(String.valueOf(item.getRate())));
            itemRateCell.setPadding(10f);
            table.addCell(itemRateCell);

            PdfPCell itemAmountCell=new PdfPCell(new Paragraph(String.valueOf(item.getAmount())));
            itemAmountCell.setPadding(10f);
            table.addCell(itemAmountCell);
        }
        document.add(table);
        document.close();
    }
}
