package com.group1.project.swp_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private int id;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private int consultantId;
    private String consultantName;
    private LocalDateTime appointmentDate;
    private int durationMinutes;
    private String status;
    private String customerNotes;
    private String consultantNotes;
    private Double consultationFee;
    private LocalDateTime createdAt;
    private PaymentDTO payment;
}
