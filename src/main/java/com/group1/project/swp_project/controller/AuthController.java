package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.*;
import com.group1.project.swp_project.dto.req.LoginRequest;
import com.group1.project.swp_project.dto.req.ResendCodeRequest;
import com.group1.project.swp_project.dto.req.ResetPasswordWithCodeRequest;
import com.group1.project.swp_project.dto.res.LoginResponse;
import com.group1.project.swp_project.service.AuthService;
import com.group1.project.swp_project.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication", description = "APIs for User Registration, Login, and Verification")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;

    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends a verification code to the email.")
    @ApiResponse(responseCode = "201", description = "User registration initiated. Please check your email for verification.")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or user/email already exists.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        try {
            authService.registerUser(registerDto);
            return new ResponseEntity<>(
                    "Đăng ký thành công! Vui lòng kiểm tra email của bạn để lấy mã kích hoạt tài khoản.",
                    HttpStatus.CREATED);
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

    @Operation(summary = "Resend verification code", description = "Generates and sends a new verification code to the user's email.")
    @ApiResponse(responseCode = "200", description = "Verification code resent successfully.")
    @ApiResponse(responseCode = "400", description = "Bad Request - User not found or already verified.")
    @PostMapping("/resend-code")

    public ResponseEntity<String> resendVerificationCode(@Valid @RequestBody ResendCodeRequest request) {
        try {
            authService.resendVerificationCode(request.getEmail());
            return ResponseEntity.ok("Mã xác thực mới đã được gửi đến email của bạn.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Request a password reset code", description = "Sends a password reset code to the user's email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ResendCodeRequest request) {
        try {
            authService.requestPasswordResetCode(request.getEmail());
            return ResponseEntity.ok("Một mã để đặt lại mật khẩu đã được gửi đến email của bạn.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Reset user password with code", description = "Sets a new password for the user using a valid code.")
    @PostMapping("/reset-password-with-code")
    public ResponseEntity<String> resetPasswordWithCode(@Valid @RequestBody ResetPasswordWithCodeRequest request) {
        try {
            authService.resetPasswordWithCode(request);
            return ResponseEntity.ok("Mật khẩu của bạn đã được đặt lại thành công.");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the current user and invalidates their session")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    @ApiResponse(responseCode = "400", description = "Bad Request - No token found or error during logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy token từ cookie
            Cookie[] cookies = request.getCookies();
            String token = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No token found in cookies");
            }

            // Vô hiệu hóa token thông qua AuthService
            authService.logout(token);

            // Xóa cookie
            Cookie jwtCookie = new Cookie("jwt", null);
            jwtCookie.setMaxAge(0);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(true);
            response.addCookie(jwtCookie);

            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error during logout: " + e.getMessage());
        }
    }
}