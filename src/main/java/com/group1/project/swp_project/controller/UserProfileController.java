package com.group1.project.swp_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group1.project.swp_project.service.ProfileService;
import com.group1.project.swp_project.dto.req.UpdateProfileRequest;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Users;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final ProfileService profileService;

    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal Users user) {
        Profile profile = profileService.getProfileByUser(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal Users user,
            @RequestBody UpdateProfileRequest profileDTO) {

        Users updatedProfile = profileService.updateProfile(user, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }
}