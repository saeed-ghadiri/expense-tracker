package com.sp.expensetracker.controller;

import com.sp.expensetracker.config.JwtUtil;
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
        account.setCreatedAt(LocalDateTime.now());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountService.saveAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninDTO singinDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(singinDTO.getName(), singinDTO.getPassword()));
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(singinDTO.getName());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> getRefreshToken(@RequestBody String refreshToken) {
        try {
            String name = jwtUtil.extractName(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(name);
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(newAccessToken);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Could not refresh token");
        }
    }
}
