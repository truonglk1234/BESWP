package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTxnRef(String txnRef);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE FUNCTION('MONTH', p.payDate) = :month " +
            "AND FUNCTION('YEAR', p.payDate) = :year " +
            "AND p.paymentStatus = 'Thành công'")
    int getTotalIncomeInMonth(@Param("month") int month, @Param("year") int year);
}
