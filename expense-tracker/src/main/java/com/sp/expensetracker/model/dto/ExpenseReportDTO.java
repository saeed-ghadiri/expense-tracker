package com.sp.expensetracker.model.dto;

import lombok.Data;

@Data
public class ExpenseReportDTO {

    private String accountName;
    private String startDate;
    private String endDate;
}
