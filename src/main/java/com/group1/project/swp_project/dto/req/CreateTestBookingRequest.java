package com.group1.project.swp_project.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTestBookingRequest {
    private Long testPackageId;
    private LocalDateTime scheduledDate;
    private String note;
    private Long consultantId;
}