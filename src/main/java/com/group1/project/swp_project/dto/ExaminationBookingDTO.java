package com.group1.project.swp_project.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ExaminationBookingDTO {
    private Long id;
    private Long userId;
    private Long serviceId;
    private int price;

    private String serviceName;
    private LocalDateTime appointmentDate;
    private String status;
    private int amount;
    private String result;

}
