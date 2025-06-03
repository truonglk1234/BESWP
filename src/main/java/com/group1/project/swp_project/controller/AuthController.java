package com.group1.project.swp_project.controller; // Đảm bảo package name đúng

import com.group1.project.swp_project.dto.RegisterDto;
import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1. Authentication", description = "APIs for User Registration and Login") // Annotation cho Swagger UI
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user",
            description = "Endpoint for new users to register an account. Provide userphone, password, full name, and email.")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data (e.g., missing fields, invalid format, userphone/email already exists)")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        try {
            User registeredUser = authService.registerUser(registerDto);
            return new ResponseEntity<>("User registered successfully! User ID: " + registeredUser.getId(), HttpStatus.CREATED);
        } catch (RuntimeException e) { // Chỉ bắt RuntimeException
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    // TODO: Thêm API đăng nhập (/login) ở đây sau này
    // @PostMapping("/login")
    // public ResponseEntity<?> loginUser(...) { ... }
}