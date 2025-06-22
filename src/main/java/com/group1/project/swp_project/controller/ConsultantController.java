package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ConsultantDTO;
import com.group1.project.swp_project.service.ConsultantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class ConsultantController {

    private final ConsultantService consultantService;

    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/consultants")
    public List<ConsultantDTO> getAllConsultants(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String gender) {
        return consultantService.getAllConsultants(specialty, gender);
    }
}
