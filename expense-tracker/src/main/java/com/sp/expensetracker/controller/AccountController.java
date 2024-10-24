package com.sp.expensetracker.controller;

import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody @Validated Account account) {
        account.setCreatedAt(LocalDateTime.now());
        accountService.saveAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.saveAccount(account));
    }
}
