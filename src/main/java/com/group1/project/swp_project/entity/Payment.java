package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
    private ExaminationBooking examinationBooking;

    private String amount;

    @Column(name = "payment_method")
    private String paymentMethod; // e.g. VNPAY

    @Column(name = "payment_status", columnDefinition = "NVARCHAR(20)")
    private String paymentStatus;

    @Column(name = "txn_ref")
    private String txnRef;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "pay_date")
    private LocalDateTime payDate;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_tran_no")
    private String bankTranNo;

    @Column(name = "vnp_transaction_no")
    private String vnpTransactionNo;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
}