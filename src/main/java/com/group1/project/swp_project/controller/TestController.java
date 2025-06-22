package com.group1.project.swp_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group1.project.swp_project.dto.req.CreateTestBookingRequest;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.service.TestBookingService;

@RestController
@RequestMapping("/api/auth/user")
public class TestController {
    private final TestBookingService testBookingService;

    public TestController(TestBookingService testBookingService) {
        this.testBookingService = testBookingService;
    }

    @PostMapping("/tests")
    public ResponseEntity<?> createTestBooking(@AuthenticationPrincipal Users user,
            @RequestBody CreateTestBookingRequest request) {
        return ResponseEntity.ok(testBookingService.createBooking(user, request));
    }

    @GetMapping("/tests")
    public ResponseEntity<?> getUserTests(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(testBookingService.getBookingsByUser(user));
    }
}
