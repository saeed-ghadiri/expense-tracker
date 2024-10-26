package com.sp.expensetracker.controller;

import com.sp.expensetracker.config.JwtUtil;
import com.sp.expensetracker.exceptions.AccountAlreadyExistsException;
import com.sp.expensetracker.model.Account;
import com.sp.expensetracker.model.dto.SigninDTO;
import com.sp.expensetracker.service.AccountService;
import com.sp.expensetracker.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AccountService accountService;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;


    public AuthController(AccountService accountService, AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Validated Account account) {
        if (accountService.accountExists(account.getName())) {
            throw new AccountAlreadyExistsException("Name already exists");
        }
        if (accountService.emailExists(account.getEmail())) {
            throw new AccountAlreadyExistsException("Email already exists");
        }
        account.setCreatedAt(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountService.saveAccount(account);
        final String jwt = jwtUtil.generateToken(account.getName());
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninDTO singinDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(singinDTO.getName(), singinDTO.getPassword()));
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(singinDTO.getName());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> getRefreshToken(@RequestBody String refreshToken) {
        try {
            String name = jwtUtil.extractName(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(name);
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails.getUsername());
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
                return ResponseEntity.ok(Map.of(
                        "accessToken", newAccessToken,
                        "refreshToken", newRefreshToken
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not refresh token");
        }
    }

}
