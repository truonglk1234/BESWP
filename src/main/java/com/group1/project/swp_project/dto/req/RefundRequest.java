package com.group1.project.swp_project.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequest {
    private Long bookingId;
    private String createdBy;
}
