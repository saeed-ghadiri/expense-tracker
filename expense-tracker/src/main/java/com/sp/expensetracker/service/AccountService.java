package com.sp.expensetracker.service;

import com.sp.expensetracker.model.Account;
import org.springframework.stereotype.Service;


public interface AccountService {

    Account saveAccount(Account account);
}
