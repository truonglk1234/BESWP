package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.UserSummary;
import com.group1.project.swp_project.service.UserManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "2. Management (Admin)", description = "APIs for Admin to manage user accounts")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserManagementService userManagementService;

    // API để lấy danh sách Managers
    @GetMapping("/managers")
    public ResponseEntity<List<UserSummary>> getManagers() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Manager"));
    }

    // API để lấy danh sách Staff
    @GetMapping("/staff")
    public ResponseEntity<List<UserSummary>> getStaff() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Staff"));
    }

    // API để lấy danh sách Consultants
    @GetMapping("/consultants")
    public ResponseEntity<List<UserSummary>> getConsultants() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Consultant"));
    }

    // API để lấy danh sách Consultants
    @GetMapping("/customers")
    public ResponseEntity<List<UserSummary>> getCustomer() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Customer"));
    }
}
