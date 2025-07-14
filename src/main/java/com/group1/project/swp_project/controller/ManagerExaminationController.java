package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ExaminationBookingView;
import com.group1.project.swp_project.service.ExaminationManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quản lý lịch xét nghiệm (Manager)")
@RestController
@RequestMapping("/api/manager/examinations")
public class ManagerExaminationController {

    private final ExaminationManagerService managerExaminationService;

    @Autowired
    public ManagerExaminationController(ExaminationManagerService managerExaminationService) {
        this.managerExaminationService = managerExaminationService;
    }

    @GetMapping
    public ResponseEntity<List<ExaminationBookingView>> getAllExaminations() {
        return ResponseEntity.ok(managerExaminationService.getAllBookings());
    }
}
