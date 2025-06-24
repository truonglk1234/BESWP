package com.group1.project.swp_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group1.project.swp_project.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.entity.Users;

@RestController
@RequestMapping("/api/auth/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping("/profile")
    // public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal
    // UserDetails userDetails) {
    // String email = userDetails.getUsername();
    // Users user = userService.getUserByEmail(email);
    // UserProfileDto profile = userService.getUserProfile(user);

    // return ResponseEntity.ok(profile);
    // }

    // @PutMapping("/profile")
    // public ResponseEntity<?> updateUserProfile(
    // @AuthenticationPrincipal UserDetails userDetails,
    // @RequestBody UserProfileDto dto) {

    // String email = userDetails.getUsername(); // username chính là email
    // Users user = userService.getUserByEmail(email);

    // userService.updateUserProfile(user, dto);
    // return ResponseEntity.ok("Cập nhật thành công");
    // }
}