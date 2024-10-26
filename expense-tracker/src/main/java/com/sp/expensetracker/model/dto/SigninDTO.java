package com.sp.expensetracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
public class SigninDTO {
    @Schema(requiredMode = REQUIRED)
    private String name;
    @Schema(requiredMode = REQUIRED)
    private String email;
    private String password;
}
