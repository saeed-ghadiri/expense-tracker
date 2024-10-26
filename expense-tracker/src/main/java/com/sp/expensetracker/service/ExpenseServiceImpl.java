package com.sp.expensetracker.service;

import com.sp.expensetracker.exceptions.AccountNotFoundException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.CategoryResultDTO;
import com.sp.expensetracker.model.dto.ExpenseAddDTO;
import com.sp.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final AccountService accountService;
    private final ExpenseRepository expenseRepository;

    List<String> predefinedAlerts = List.of(
            "Spending too much on ",
            "Consider reducing your expenses for ",
            "You're exceeding your budget for",
            "Try to save more on ",
            "Review your recent shopping expenses by "
    );

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, AccountService accountService) {
        this.expenseRepository = expenseRepository;
        this.accountService = accountService;
    }

    @Override
    public void addExpense(ExpenseAddDTO expenseAddDTO) {
        Account account = accountService.findByName(expenseAddDTO.getAccountName())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Expense expense = new Expense();
        expense.setAmount(expenseAddDTO.getAmount());
        expense.setCategory(expenseAddDTO.getCategory());
        expense.setDescription(expenseAddDTO.getDescription());
        expense.setDate(expenseAddDTO.getDate());
        expense.setAccount(account);
        expenseRepository.save(expense);
    }

    public List<Expense> queryExpenses(String accountName, LocalDate startDate, LocalDate endDate) {
        accountService.findByName(accountName)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return expenseRepository.findExpensesByAccountNameAndDateRange(accountName, startDate, endDate);
    }

    public Map<String, BigDecimal> groupExpenses(List<Expense> expenses, Function<Expense, String> groupBy) {
        return expenses.stream()
                .collect(Collectors.groupingBy(groupBy,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));
    }

    public List<CategoryResultDTO> sortAndSelectTopCategories(Map<String, BigDecimal> categoryTotals, int count) {
        return categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> CategoryResultDTO.builder().category(entry.getKey()).totalAmount(entry.getValue()).build())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void insertAlerts(List<CategoryResultDTO> topCategories) {
        Random random = new Random();
        for (CategoryResultDTO summary : topCategories) {
            summary.setMessage(predefinedAlerts.get(random.nextInt(predefinedAlerts.size())) + summary.getCategory() );
        }
    }

    @Override
    public List<CategoryResultDTO> reportAndAlertByCategory( String accountName, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = queryExpenses(accountName, startDate, endDate);
        Map<String, BigDecimal> categoryTotals = groupExpenses(expenses, Expense::getCategory);
        List<CategoryResultDTO> top3Categories = sortAndSelectTopCategories(categoryTotals, 3);
        insertAlerts(top3Categories);
        return top3Categories;
    }
}