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
public class BookingRequest {
    private int consultantId;
    private int scheduleId;
    private LocalDateTime appointmentDate;

    private String customerNotes;
    private String paymentMethod;
}
