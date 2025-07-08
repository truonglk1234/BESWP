package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "examination_payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_booking_id")
    private ExaminationBooking examinationBooking;

    private int amount;
    private String paymentMethod;
    private String paymentStatus; // PENDING, SUCCESS, FAILED
    @Column(unique = true)
    private String txnRef;

    private String vnpTransactionNo;
    private String bankCode;
    private String bankTranNo;
    private LocalDateTime payDate;
}

