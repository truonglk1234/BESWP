package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "examination_feedback")
public class ExaminationFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    private ExaminationBooking booking;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id")
    private Users consultant;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServicePrice service;

    private int rating;

    @Column(name = "comment", columnDefinition = "NVARCHAR(500)")
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}