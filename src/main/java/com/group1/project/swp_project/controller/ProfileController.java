package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.dto.req.UpdateProfileDto;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth/profileuser")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService ProfileService) {
        this.profileService = ProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);
        UserProfileDto profile = profileService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UpdateProfileDto dto) {

        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);
        profileService.updateUserProfile(user, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }
}
