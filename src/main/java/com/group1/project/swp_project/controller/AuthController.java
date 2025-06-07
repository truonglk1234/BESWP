package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.LoginRequest;
import com.group1.project.swp_project.dto.LoginResponse;
import com.group1.project.swp_project.dto.RegisterDto;
import com.group1.project.swp_project.dto.VerificationRequest;
import com.group1.project.swp_project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication", description = "APIs for User Registration, Login, and Verification")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends a verification code to the email.")
    @ApiResponse(responseCode = "201", description = "User registration initiated. Please check your email for verification.")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or user/email already exists.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        try {
            authService.registerUser(registerDto);
            return new ResponseEntity<>("Đăng ký thành công! Vui lòng kiểm tra email của bạn để lấy mã kích hoạt tài khoản.", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token.")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid credentials or account not enabled.")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Verify user account with code", description = "Activates a user account using the verification code from email.")
    @ApiResponse(responseCode = "200", description = "Account verified successfully.")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid email or code, or token expired.")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody VerificationRequest verificationRequest) {
        try {
            authService.verifyCode(verificationRequest);
            return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công!");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}