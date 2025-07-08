package com.group1.project.swp_project.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class ExaminationBookingRequest {
    private Integer serviceId;
    private LocalDateTime appointmentDate;
    private String name;
    private String phone;
    private String email;
    private String note;
}
