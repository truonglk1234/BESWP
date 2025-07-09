package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ConsultantDTO;
import com.group1.project.swp_project.service.ConsultantService;
import com.group1.project.swp_project.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/consultants")
    public ResponseEntity<List<ConsultantDTO>> getAllConsultants(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String gender) {
        var c = this.consultantService.getAllConsultants(specialty, gender);
        return ResponseEntity.ok().body(c);
    }
}
