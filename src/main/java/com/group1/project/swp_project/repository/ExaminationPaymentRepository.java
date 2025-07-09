package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExaminationPaymentRepository extends JpaRepository<ExaminationPayment, Long> {
    Optional<ExaminationPayment> findByTxnRef(String txnRef);
}
