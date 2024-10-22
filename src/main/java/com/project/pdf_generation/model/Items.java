package com.project.pdf_generation.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Items {
    private String name;
    private String quantity;
    private double rate;
    private double amount;
}
