package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<ExaminationPayment, Long> {
    Optional<ExaminationPayment> findByTxnRef(String txnRef);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM ExaminationPayment p " +
            "WHERE FUNCTION('MONTH', p.payDate) = :month " +
            "AND FUNCTION('YEAR', p.payDate) = :year " +
            "AND p.paymentStatus = 'Thành công'")
    int getTotalIncomeInMonth(@Param("month") int month, @Param("year") int year);

    @Query(value = """
    SELECT 
        'T' + CAST(MONTH(p.pay_date) AS VARCHAR) + '/' + CAST(YEAR(p.pay_date) AS VARCHAR) AS month_label,
        SUM(p.amount) AS total_revenue
    FROM payments p
    WHERE p.payment_status = N'Thành công'
        AND p.pay_date IS NOT NULL
    GROUP BY YEAR(p.pay_date), MONTH(p.pay_date)
    ORDER BY YEAR(p.pay_date), MONTH(p.pay_date)
    """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue();
}
