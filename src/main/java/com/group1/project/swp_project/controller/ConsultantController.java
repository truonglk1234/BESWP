package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ConsultantDTO;
import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.service.ConsultantService;
import com.group1.project.swp_project.service.ProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class ConsultantController {

    private final ConsultantService consultantService;
    private final ProfileService profileService;

    public ConsultantController(ConsultantService consultantService, ProfileService profileService) {
        this.consultantService = consultantService;
        this.profileService = profileService;
    }

    @GetMapping("/consultants")
    public ResponseEntity<List<ConsultantDTO>> getAllConsultants(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String gender) {
        var c = this.consultantService.getAllConsultants(specialty, gender);
        return ResponseEntity.ok().body(c);
    }

    @GetMapping("/consultants/{id}")
    public ResponseEntity<UserProfileDto> getConsultantById(@PathVariable Integer id) {
        var user = profileService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        var dto = profileService.getUserProfile(user);
        return ResponseEntity.ok(dto);
    }
}
