package com.group1.project.swp_project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExaminationFeedbackView {
    private Long bookingId;
    private String fullName;
    private int rating;
    private String comment;
    private String serviceName;
    private LocalDateTime createdAt;
}
