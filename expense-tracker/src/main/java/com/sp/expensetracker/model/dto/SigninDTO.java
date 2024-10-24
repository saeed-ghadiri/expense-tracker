package com.sp.expensetracker.model.dto;

import lombok.Data;

@Data
public class SigninDTO {
    private String name;
    private String email;
    private String password;
}
