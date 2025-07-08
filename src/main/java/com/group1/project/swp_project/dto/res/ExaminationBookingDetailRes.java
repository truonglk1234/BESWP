package com.group1.project.swp_project.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExaminationBookingDetailRes {
    private Long id;
    private String serviceName;
    private int price;
    private LocalDateTime appointmentDate;

    private String name;
    private String phone;
    private String email;
    private String note;

    private String status;
}
