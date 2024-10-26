package com.sp.expensetracker.service;

import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.CategoryResultDTO;
import com.sp.expensetracker.model.dto.ExpenseAddDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    void addExpense(ExpenseAddDTO  expenseAddDTO);

    List<CategoryResultDTO> reportAndAlertByCategory(String accountName, LocalDate startDate, LocalDate endDate);
}
