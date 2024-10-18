package com.project.pdf_generation.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class InvoiceDetails {
    private String seller;
    private String sellerGSTIN;
    private String sellerAddress;
    private String buyer;
    private String buyerGSTIN;
    private String buyerAddress;
    private List<Items> items;

    @Getter
    @Setter
    public static class Items {
        private String name;
        private String quantity;
        private double rate;
        private double amount;
    }
}
