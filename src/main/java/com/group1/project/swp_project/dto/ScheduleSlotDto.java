package com.group1.project.swp_project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleSlotDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double pricePerSession;
    private Integer durationMinutes;
    private Boolean isAvailable;
    private String note;
}
