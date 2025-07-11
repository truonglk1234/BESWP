package com.group1.project.swp_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.group1.project.swp_project.entity.Schedule.DayOfWeek;
import com.group1.project.swp_project.entity.Schedule.ScheduleType;

@Data
public class ScheduleDTO {
    private Long id;
    private ScheduleType type;
    private DayOfWeek dayOfWeek;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private Integer durationMinutes;
    private String note;
    private Boolean isAvailable;
    private int consultantId;
}