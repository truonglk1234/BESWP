package com.group1.project.swp_project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExaminationResultResponse {
    private String serviceName;
    private LocalDateTime appointmentDate;
    private String status;
    private String result;
    private String advice;
}
