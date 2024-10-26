package com.sp.expensetracker.service;

import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    public CustomUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Account account = accountService.findByName(name)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return new org.springframework.security.core.userdetails.User(account.getName(), account.getPassword(), new ArrayList<>());
    }
}

