package com.sp.expensetracker.service;

import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.CategoryResultDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    void addExpense(Expense expense);

    List<CategoryResultDTO> reportAndAlertByCategory(String email, LocalDate startDate, LocalDate endDate);
}
