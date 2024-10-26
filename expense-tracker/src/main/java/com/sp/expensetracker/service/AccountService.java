package com.sp.expensetracker.service;

import com.sp.expensetracker.model.Account;

import java.util.Optional;


public interface AccountService {

    boolean accountExists(String username);

    boolean emailExists(String email);

    Account saveAccount(Account account);

    Optional<Account> findByName(String name);
}
