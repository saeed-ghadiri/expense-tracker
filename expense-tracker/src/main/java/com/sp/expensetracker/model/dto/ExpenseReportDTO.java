package com.sp.expensetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class ExpenseReportDTO {

    @Schema(requiredMode = REQUIRED)
    private String accountName;
    @Schema(requiredMode = REQUIRED)
    private String startDate;
    @Schema(requiredMode = REQUIRED)
    private String endDate;
}
