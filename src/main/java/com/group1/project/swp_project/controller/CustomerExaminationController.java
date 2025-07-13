package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ExaminationResultResponse;
import com.group1.project.swp_project.service.ExaminationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/customer/examinations")
public class CustomerExaminationController {

    @Autowired
    private ExaminationResultService examinationResultService;


    @GetMapping("/{bookingId}/result")
    public ResponseEntity<ExaminationResultResponse> getTestResult(
            @PathVariable Long bookingId,
            Principal principal
    ) {
        String email = principal.getName();
        ExaminationResultResponse result = examinationResultService.getResultForCustomer(bookingId, email);
        return ResponseEntity.ok(result);
    }
}

