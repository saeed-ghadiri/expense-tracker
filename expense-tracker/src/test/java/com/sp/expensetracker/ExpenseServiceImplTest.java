package com.sp.expensetracker;

import com.sp.expensetracker.exceptions.AccountNotFoundException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.ExpenseAddDTO;
import com.sp.expensetracker.repository.ExpenseRepository;
import com.sp.expensetracker.service.AccountService;
import com.sp.expensetracker.service.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExpenseServiceImplTest {

    @Autowired
    private ExpenseServiceImpl expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private AccountService accountService;

    private ExpenseAddDTO expenseAddDTO;
    private Account account;
    private Expense expense;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setName("user4");

        expenseAddDTO = new ExpenseAddDTO();
        expenseAddDTO.setAccountName("user4");
        expenseAddDTO.setAmount(new BigDecimal("100.00"));
        expenseAddDTO.setCategory("Food");
        expenseAddDTO.setDate(LocalDate.now());

        expense = new Expense();
        expense.setId(1L);
        expense.setAccount(account);
        expense.setAmount(expenseAddDTO.getAmount());
        expense.setCategory(expenseAddDTO.getCategory());
        expense.setDate(expenseAddDTO.getDate());
    }

    @Test
    void addExpense_shouldSaveExpense_whenAccountExists() {
        when(accountService.findByName(expenseAddDTO.getAccountName())).thenReturn(Optional.of(account));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        expenseService.addExpense(expenseAddDTO);

        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void addExpense_shouldThrowException_whenAccountDoesNotExist() {
        when(accountService.findByName(expenseAddDTO.getAccountName())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.addExpense(expenseAddDTO))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void queryExpenses_shouldReturnExpenses_whenAccountExists() {
        LocalDate startDate = LocalDate.of(2024, 3, 11);
        LocalDate endDate = LocalDate.of(2024, 3, 14);
        when(accountService.findByName(account.getName())).thenReturn(Optional.of(account));
        when(expenseRepository.findExpensesByAccountNameAndDateRange(account.getName(), startDate, endDate))
                .thenReturn(List.of(expense));

        List<Expense> expenses = expenseService.queryExpenses(account.getName(), startDate, endDate);

        assertThat(expenses).isNotEmpty();
        assertThat(expenses).contains(expense);
        verify(expenseRepository, times(1)).findExpensesByAccountNameAndDateRange(account.getName(), startDate, endDate);
    }

    @Test
    void groupExpenses_shouldGroupExpensesByCategory() {
        List<Expense> expenses = List.of(expense);

        Map<String, BigDecimal> groupedExpenses = expenseService.groupExpenses(expenses, Expense::getCategory);

        assertThat(groupedExpenses).containsEntry("Food", new BigDecimal("100.00"));
    }

    @Test
    void sortAndSelectTopCategories_shouldReturnTopCategories() {
        Map<String, BigDecimal> categoryTotals = Map.of(
                "Food", new BigDecimal("200.00"),
                "Transport", new BigDecimal("150.00"),
                "Entertainment", new BigDecimal("120.00")
        );

        var topCategories = expenseService.sortAndSelectTopCategories(categoryTotals, 2);

        assertThat(topCategories).hasSize(2);
        assertThat(topCategories.get(0).getCategory()).isEqualTo("Food");
        assertThat(topCategories.get(0).getTotalAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(topCategories.get(1).getCategory()).isEqualTo("Transport");
    }

    @Test
    void insertAlerts_shouldAddAlertsToTopCategories() {
        var categoryResultDTOs = expenseService.sortAndSelectTopCategories(
                Map.of("Food", new BigDecimal("200.00")), 1);

        expenseService.insertAlerts(categoryResultDTOs);

        assertThat(categoryResultDTOs.get(0).getMessage()).isNotEmpty();
    }
}

