package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "examination_results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExaminationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private ExaminationBooking booking;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String result;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String advice;

    private String status;
}
