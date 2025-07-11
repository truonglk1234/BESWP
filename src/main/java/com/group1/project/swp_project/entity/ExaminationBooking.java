package com.group1.project.swp_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "examination_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    @JsonIgnore
    private ServicePrice service;
    private LocalDateTime appointmentDate;
    @Column(name = "name", columnDefinition = "NVARCHAR(200)")
    private String name;
    @Column(name = "phone", columnDefinition = "NVARCHAR(11)")
    private String phone;
    @Column(name = "email", columnDefinition = "NVARCHAR(200)")
    private String email;
    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
    @OneToOne(mappedBy = "examinationBooking", cascade = CascadeType.ALL)
    private Payment payment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    @JsonIgnore
    private Users assignedStaff;
    @Column(name = "status", columnDefinition = "NVARCHAR(MAX)")
    private String status;

}
