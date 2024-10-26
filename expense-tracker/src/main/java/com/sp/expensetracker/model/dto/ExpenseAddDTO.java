package com.sp.expensetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class ExpenseAddDTO {
    @Schema(requiredMode = REQUIRED)
    private BigDecimal amount;
    @Schema(requiredMode = REQUIRED)
    private String category;
    private String description;
    @Schema(requiredMode = REQUIRED)
    private LocalDate date;
    private LocalTime time;
    @Schema(requiredMode = REQUIRED)
    private String accountName;
}
