package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.UserSummary;
import com.group1.project.swp_project.dto.req.UpdateProfileRequest;
import com.group1.project.swp_project.service.UserManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3.Management (Manager)", description = "APIs for Manager to manage staff and consultants")
@RestController
@RequestMapping("/api/auth/manager")
@PreAuthorize("hasAuthority('Manager')")
public class ManagerController {

    private final UserManagementService userManagementService;

    @Autowired
    public ManagerController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
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

    // API để lấy danh sách Customers
    @GetMapping("/customers")
    public ResponseEntity<List<UserSummary>> getCustomers() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Customer"));
    }

    // API cập nhật thông tin Staff
    @PutMapping("/staff/{id}")
    public ResponseEntity<UserSummary> updateStaff(@PathVariable int id, @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    }

    // API cập nhật thông tin Consultant
    @PutMapping("/consultants/{id}")
    public ResponseEntity<UserSummary> updateConsultant(@PathVariable int id,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    }

    // API cập nhật thông tin Customer
    @PutMapping("/customers/{id}")
    public ResponseEntity<UserSummary> updateCustomer(@PathVariable int id, @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    }

    // API xóa Staff
    @DeleteMapping("/staff/{id}")
    public ResponseEntity<UserSummary> deleteStaff(@PathVariable int id) {
        return ResponseEntity.ok(userManagementService.deleteUser(id));
    }

    // API xóa Consultant
    @DeleteMapping("/consultants/{id}")
    public ResponseEntity<UserSummary> deleteConsultant(@PathVariable int id) {
        return ResponseEntity.ok(userManagementService.deleteUser(id));
    }

    // API xóa Customer
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<UserSummary> deleteCustomer(@PathVariable int id) {
        return ResponseEntity.ok(userManagementService.deleteUser(id));
    }
}
