package com.sp.expensetracker.service;

import com.sp.expensetracker.exceptions.AccountAlreadyExistsException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean accountExists(String username) {
        return accountRepository.existsByName(username);
    }

    @Override
    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findByName(String name) {
        return accountRepository.findByName(name);
    }
}
