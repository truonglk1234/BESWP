package com.group1.project.swp_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
public class ExaminationPaymentDTO {
    private Long id;
    private Long bookingId;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
}
