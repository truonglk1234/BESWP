package com.group1.project.swp_project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentTime;

    @Column(length = 255)
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // nếu có chọn tư vấn viên:
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;

    @Column(name = "status") // pending, confirmed, cancelled
    private String status;
}
