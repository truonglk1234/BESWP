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

@Tag(name = "2. Management (Admin)", description = "APIs for Admin to manage user accounts")
@RestController
@RequestMapping("/api/auth/admin")
@PreAuthorize("hasAuthority('Admin')")

public class AdminController {
    @Autowired
    private UserManagementService userManagementService;

    // API to get list of Managers
    @GetMapping("/managers")
    public ResponseEntity<List<UserSummary>> getManagers() {
        return ResponseEntity.ok(userManagementService.getUsersByRole("Manager"));
    }

    // // API to get list of Staff
    // @GetMapping("/staff")
    // public ResponseEntity<List<UserSummary>> getStaff() {
    // return ResponseEntity.ok(userManagementService.getUsersByRole("Staff"));
    // }
    //
    // // API to get list of Consultants
    // @GetMapping("/consultants")
    // public ResponseEntity<List<UserSummary>> getConsultants() {
    // return ResponseEntity.ok(userManagementService.getUsersByRole("Consultant"));
    // }
    //
    // // API to get list of Customers
    // @GetMapping("/customers")
    // public ResponseEntity<List<UserSummary>> getCustomer() {
    // return ResponseEntity.ok(userManagementService.getUsersByRole("Customer"));
    // }
    //
    // // API to update Manager profile
    // @PutMapping("/managers/{id}")
    // public ResponseEntity<UserSummary> updateManager(@PathVariable int id,
    // @RequestBody UpdateProfileRequest request) {
    // return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    // }
    //
    // // API to update Staff profile
    // @PutMapping("/staff/{id}")
    // public ResponseEntity<UserSummary> updateStaff(@PathVariable int id,
    // @RequestBody UpdateProfileRequest request) {
    // return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    // }
    //
    // // API to update Consultant profile
    // @PutMapping("/consultants/{id}")
    // public ResponseEntity<UserSummary> updateConsultant(@PathVariable int id,
    // @RequestBody UpdateProfileRequest request) {
    // return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    // }
    //
    // // API to update Customer profile
    // @PutMapping("/customers/{id}")
    // public ResponseEntity<UserSummary> updateCustomer(@PathVariable int id,
    // @RequestBody UpdateProfileRequest request) {
    // return ResponseEntity.ok(userManagementService.updateProfile(id, request));
    // }
    //
    // @DeleteMapping("/managers/{id}")
    // public ResponseEntity<UserSummary> deleteManager(@PathVariable int id) {
    // return ResponseEntity.ok(userManagementService.deleteUser(id));
    // }
    //
    // @DeleteMapping("/staff/{id}")
    // public ResponseEntity<UserSummary> deleteStaff(@PathVariable int id) {
    // return ResponseEntity.ok(userManagementService.deleteUser(id));
    // }
    //
    // @DeleteMapping("/consultants/{id}")
    // public ResponseEntity<UserSummary> deleteConsultant(@PathVariable int id) {
    // return ResponseEntity.ok(userManagementService.deleteUser(id));
    // }
    //
    // @DeleteMapping("/customers/{id}")
    // public ResponseEntity<UserSummary> deleteCustomer(@PathVariable int id) {
    // return ResponseEntity.ok(userManagementService.deleteUser(id));
    // }
}
