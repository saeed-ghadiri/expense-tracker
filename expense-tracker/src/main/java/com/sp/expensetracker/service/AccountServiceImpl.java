package com.sp.expensetracker.service;

import com.sp.expensetracker.exceptions.AccountAlreadyExistsException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean accountExists(String username) {
        return accountRepository.existsByName(username);
    }

    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(email);
    }

    public Account saveAccount(Account account) {
        if (accountExists(account.getName())) {
            throw new AccountAlreadyExistsException("Name already exists");
        }
        if (emailExists(account.getEmail())) {
            throw new AccountAlreadyExistsException("Email already exists");
        }
        return accountRepository.save(account);
    }
}
