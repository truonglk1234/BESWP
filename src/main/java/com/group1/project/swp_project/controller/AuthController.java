package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.*;
import com.group1.project.swp_project.dto.req.*;
import com.group1.project.swp_project.dto.res.LoginResponse;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.entity.VerificationToken;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.repository.UserStatusRepository;
import com.group1.project.swp_project.repository.VerificationTokenRepository;
import com.group1.project.swp_project.service.AuthService;
import com.group1.project.swp_project.security.JwtUtil;
import com.group1.project.swp_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

@Tag(name = "1. Authentication", description = "APIs for User Registration, Login, and Verification")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, UserService userService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userService = userService;
        this.jwtUtil =  jwtUtil;
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
        String email = verificationRequest.getEmail();
        String code = verificationRequest.getCode();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));


        VerificationToken token = verificationTokenRepository.findByUser(user);
        if (token == null || !token.getToken().equals(code)) {
            return ResponseEntity.badRequest().body("Mã xác thực không chính xác.");
        }

        if (token.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Mã xác thực đã hết hạn.");
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }

        verificationTokenRepository.delete(token);

        return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công! Vui lòng đăng nhập lại.");
    }

    @Operation(summary = "Reset user password with code", description = "Sets a new password for the user using a valid code.")
    @PostMapping("/reset-password-with-code")
    public ResponseEntity<String> resetPasswordWithCode(
            @Valid @RequestBody ResetPasswordWithCodeRequest request) {
        try {
            authService.resetPasswordWithCode(request);
            return ResponseEntity.ok("Mật khẩu của bạn đã được đặt lại thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @Operation(summary = "Resend verification or reset password code", description = "Gửi lại mã xác thực hoặc mã quên mật khẩu.")
    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@RequestBody ResendCodeRequest request) {
        try {
            String type = request.getType();
            if ("register".equalsIgnoreCase(type)) {
                authService.resendVerificationCode(request.getEmail());
                return ResponseEntity.ok("✅ Mã xác thực tài khoản đã được gửi lại.");
            } else if ("reset-password".equalsIgnoreCase(type)) {
                authService.requestPasswordResetCode(request.getEmail());
                return ResponseEntity.ok("✅ Mã đặt lại mật khẩu đã được gửi lại.");
            } else {
                return ResponseEntity.badRequest().body("Loại mã không hợp lệ. Hãy dùng 'register' hoặc 'reset-password'.");
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody @Valid VerifyResetCodeRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));

        VerificationToken token = verificationTokenRepository.findByUser(user);
        if (token == null || !token.getToken().equals(request.getCode()) || token.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body("Mã xác thực không hợp lệ hoặc đã hết hạn.");
        }

        return ResponseEntity.ok("Xác thực thành công.");
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") @NotNull String bearerToken,
            @RequestBody ChangePasswordRequest request
    ) {
        String token = bearerToken.substring(7);
        String email = jwtUtil.extractUserEmail(token);

        authService.changePasswordByEmail(email, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

   
}