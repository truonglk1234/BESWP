package com.group1.project.swp_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExaminationBookingView {
    private Long id;
    private String userFullName;
    private String serviceName;
    private LocalDateTime appointmentDate;
    private String status;
}
