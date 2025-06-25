package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="MenstrualCycle")
@Entity
public class MenstrualCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private Users user;

    @Column(name = "cycle_start_date")
    private LocalDate cycleStartDate;

    @Column(name = "cycle_length")
    private Integer cycleLength;

    @Column(name = "period_length")
    private Integer periodLength;

    @Column(name = "remind_ovulation")
    private Boolean remindOvulation;

    @Column(name = "remind_high_fertile")
    private Boolean remindHighFertile;

    @Column(name = "remind_pill")
    private Boolean remindPill;

    @Column(name = "pill_time")
    private LocalTime pillTime;

    @Column(name = "note", columnDefinition = "nvarchar(200)")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
