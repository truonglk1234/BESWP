package com.group1.project.swp_project.dto.req;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Long bookingId;
    private int rating;
    private String comment;
}
