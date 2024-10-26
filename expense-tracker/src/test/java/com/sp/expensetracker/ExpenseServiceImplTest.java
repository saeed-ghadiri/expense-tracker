package com.sp.expensetracker;

import com.sp.expensetracker.exceptions.AccountNotFoundException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.model.Expense;
import com.sp.expensetracker.model.dto.CategoryResultDTO;
import com.sp.expensetracker.model.dto.ExpenseAddDTO;
import com.sp.expensetracker.repository.ExpenseRepository;
import com.sp.expensetracker.service.AccountService;
import com.sp.expensetracker.service.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Account account;
    private Expense expense;
    private ExpenseAddDTO expenseAddDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup sample data
        account = new Account();
        account.setName("testAccount");

        expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(50));
        expense.setCategory("Food");
        expense.setDescription("Dinner");
        expense.setDate(LocalDate.now());
        expense.setAccount(account);

        expenseAddDTO = new ExpenseAddDTO();
        expenseAddDTO.setAccountName("testAccount");
        expenseAddDTO.setAmount(BigDecimal.valueOf(50));
        expenseAddDTO.setCategory("Food");
        expenseAddDTO.setDescription("Dinner");
        expenseAddDTO.setDate(LocalDate.now());
    }

    @Test
    void addExpense_ShouldAddExpense() {
        when(accountService.findByName(anyString())).thenReturn(Optional.of(account));

        expenseService.addExpense(expenseAddDTO);

        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void addExpense_ShouldThrowException_WhenAccountNotFound() {
        when(accountService.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> expenseService.addExpense(expenseAddDTO));
    }

    @Test
    void queryExpenses_ShouldReturnExpenses_WhenAccountExists() {
        when(accountService.findByName(anyString())).thenReturn(Optional.of(account));
        when(expenseRepository.findExpensesByAccountNameAndDateRange(anyString(), any(), any()))
                .thenReturn(List.of(expense));

        List<Expense> expenses = expenseService.queryExpenses("testAccount", LocalDate.now().minusDays(1), LocalDate.now());

        assertFalse(expenses.isEmpty());
        assertEquals(1, expenses.size());
        verify(expenseRepository, times(1)).findExpensesByAccountNameAndDateRange(anyString(), any(), any());
    }

    @Test
    void queryExpenses_ShouldThrowException_WhenAccountNotFound() {
        when(accountService.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> expenseService.queryExpenses("testAccount", LocalDate.now().minusDays(1), LocalDate.now()));
    }

    @Test
    void groupExpenses_ShouldGroupExpensesByCategory() {
        List<Expense> expenses = List.of(expense);

        Map<String, BigDecimal> groupedExpenses = expenseService.groupExpenses(expenses, Expense::getCategory);

        assertEquals(1, groupedExpenses.size());
        assertEquals(BigDecimal.valueOf(50), groupedExpenses.get("Food"));
    }

    @Test
    void sortAndSelectTopCategories_ShouldReturnTopCategories() {
        Map<String, BigDecimal> categoryTotals = Map.of(
                "Food", BigDecimal.valueOf(50),
                "Transport", BigDecimal.valueOf(30),
                "Entertainment", BigDecimal.valueOf(20)
        );

        List<CategoryResultDTO> topCategories = expenseService.sortAndSelectTopCategories(categoryTotals, 2);

        assertEquals(2, topCategories.size());
        assertEquals("Food", topCategories.get(0).getCategory());
        assertEquals(BigDecimal.valueOf(50), topCategories.get(0).getTotalAmount());
    }

    @Test
    void insertAlerts_ShouldAddAlertsToCategories() {
        CategoryResultDTO category1 = CategoryResultDTO.builder().category("Food").totalAmount(BigDecimal.valueOf(50)).build();
        CategoryResultDTO category2 = CategoryResultDTO.builder().category("Transport").totalAmount(BigDecimal.valueOf(30)).build();
        List<CategoryResultDTO> topCategories = List.of(category1, category2);

        expenseService.insertAlerts(topCategories);

        assertNotNull(category1.getMessage());
        assertTrue(category1.getMessage().contains("Food"));
        assertNotNull(category2.getMessage());
        assertTrue(category2.getMessage().contains("Transport"));
    }

    @Test
    void reportAndAlertByCategory_ShouldReturnTopCategoriesWithAlerts() {
        when(accountService.findByName(anyString())).thenReturn(Optional.of(account));
        when(expenseRepository.findExpensesByAccountNameAndDateRange(anyString(), any(), any())).thenReturn(List.of(expense));

        List<CategoryResultDTO> result = expenseService.reportAndAlertByCategory("testAccount", LocalDate.now().minusDays(1), LocalDate.now());

        assertFalse(result.isEmpty());
        assertEquals("Food", result.get(0).getCategory());
        assertNotNull(result.get(0).getMessage());
        verify(expenseRepository, times(1)).findExpensesByAccountNameAndDateRange(anyString(), any(), any());
    }
}
