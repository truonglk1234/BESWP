package com.group1.project.swp_project.dto.req;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateAppointmentRequest {
    private LocalDateTime appointmentTime;
    private String note;
    private Long consultantId;
}