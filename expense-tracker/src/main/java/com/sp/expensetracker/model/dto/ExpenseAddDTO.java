package com.sp.expensetracker.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExpenseAddDTO {

    private Long id;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String accountName;
}
