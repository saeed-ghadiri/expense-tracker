package com.sp.expensetracker.controller;

import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.CategoryResultDTO;
import com.sp.expensetracker.model.dto.ExpenseAddDTO;
import com.sp.expensetracker.model.dto.ExpenseReportDTO;
import com.sp.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/expense")
public class ExpenseController {


    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addExpense(@RequestBody ExpenseAddDTO expenseAddDTO) {
        expenseService.addExpense(expenseAddDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Expense added successfully");
    }

    @PostMapping("/report")
    public List<CategoryResultDTO> reportExpense(@RequestBody ExpenseReportDTO expenseReportDTO) {
    return expenseService.reportAndAlertByCategory(expenseReportDTO.getAccountName(), LocalDate.parse(expenseReportDTO.getStartDate()), LocalDate.parse(expenseReportDTO.getEndDate()));
    }



}
