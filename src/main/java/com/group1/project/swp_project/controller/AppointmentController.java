package com.group1.project.swp_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group1.project.swp_project.dto.req.CreateAppointmentRequest;
import com.group1.project.swp_project.entity.Appointment;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.service.AppointmentService;

@RestController
@RequestMapping("/api/auth/user")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(@AuthenticationPrincipal Users user,
            @RequestBody CreateAppointmentRequest request) {
        Appointment app = appointmentService.createAppointment(user, request);
        return ResponseEntity.ok(app);
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUser(user));
    }
}
