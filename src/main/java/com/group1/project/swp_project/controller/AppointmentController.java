package com.group1.project.swp_project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group1.project.swp_project.dto.AppointmentDto;
import com.group1.project.swp_project.dto.BookingRequest;
import com.group1.project.swp_project.dto.PaymentDTO;
import com.group1.project.swp_project.repository.ScheduleRepository;
import com.group1.project.swp_project.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")

public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService, ScheduleRepository scheduleRepo) {
        this.appointmentService = appointmentService;

    }

    @Operation(summary = "Book an appointment", description = "Create a new appointment booking")
    @PostMapping("/book/{customerId}")
    public ResponseEntity<?> bookAppointment(
            @PathVariable int customerId,
            @RequestBody BookingRequest request) {
        try {
            AppointmentDto appointment = appointmentService.bookAppointment(customerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get customer appointments", description = "Get all appointments for a specific customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AppointmentDto>> getCustomerAppointments(@PathVariable int customerId) {
        try {
            List<AppointmentDto> appointments = appointmentService.getCustomerAppointments(customerId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Get consultant appointments", description = "Get all appointments for a specific consultant")
    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<List<AppointmentDto>> getConsultantAppointments(@PathVariable int consultantId) {
        try {
            List<AppointmentDto> appointments = appointmentService.getConsultantAppointments(consultantId);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "Update appointment status", description = "Update the status of an appointment")
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable int appointmentId,
            @RequestParam String status) {
        try {
            AppointmentDto appointment = appointmentService.updateAppointmentStatus(appointmentId, status);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update payment status", description = "Update the payment status of an appointment")
    @PutMapping("/{appointmentId}/payment/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable int appointmentId,
            @RequestParam String paymentStatus) {
        try {
            PaymentDTO payment = appointmentService.updatePaymentStatus(appointmentId, paymentStatus);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
