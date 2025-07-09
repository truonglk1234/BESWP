package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_bookings")
public class TestBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime scheduledDate;

    @Column(length = 255)
    private String note;

    @Column(length = 50)
    private String status; // pending, confirmed, cancelled

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "test_package_id")
    private TestPackage testPackage; // bạn sẽ cần thêm bảng gói xét nghiệm

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Users consultant;
}
