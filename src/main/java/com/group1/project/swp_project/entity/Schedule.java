package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double price;

    private Integer durationMinutes;

    private String note;

    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant_id")
    private Users consultant;

    public enum ScheduleType {
        HANG_TUAN,
        HANG_NGAY
    }

    public enum DayOfWeek {
        THU_HAI,
        THU_BA,
        THU_TU,
        THU_NAM,
        THU_SAU,
        THU_BAY,
        CHU_NHAT
    }
}