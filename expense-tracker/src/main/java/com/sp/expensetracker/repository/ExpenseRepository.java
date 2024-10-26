package com.sp.expensetracker.repository;

import com.sp.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM expense e JOIN e.account a " +
            "WHERE a.name = :accountName " +
            "AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findExpensesByAccountNameAndDateRange(
            @Param("accountName") String accountName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

