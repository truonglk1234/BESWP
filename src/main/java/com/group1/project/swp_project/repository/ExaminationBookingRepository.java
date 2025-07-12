package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.dto.MonthlyTestStat;
import com.group1.project.swp_project.entity.ExaminationBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExaminationBookingRepository extends JpaRepository<ExaminationBooking, Long> {
        List<ExaminationBooking> findByUserId(Long userId);

        List<ExaminationBooking> findAll();

        @Query("SELECT e FROM ExaminationBooking e " +
                        "JOIN FETCH e.service s " +
                        "WHERE e.id = :id")
        Optional<ExaminationBooking> findDetailedById(@Param("id") Long id);

        List<ExaminationBooking> findByAssignedStaff_IdAndStatusIn(Long staffId, List<String> statuses);
        long countByAssignedStaffIsNotNull();

        @Query("SELECT COUNT(e) FROM ExaminationBooking e " +
                "WHERE FUNCTION('MONTH', e.appointmentDate) = :month " +
                "AND FUNCTION('YEAR', e.appointmentDate) = :year")
        long countExaminationsInMonth(@Param("month") int month, @Param("year") int year);

        @Query(value = "SELECT YEAR(appointment_date), MONTH(appointment_date), COUNT(*) " +
                "FROM examination_bookings " +
                "WHERE LTRIM(RTRIM(status)) = N'Đã trả kết quả' AND appointment_date IS NOT NULL " +
                "GROUP BY YEAR(appointment_date), MONTH(appointment_date) " +
                "ORDER BY YEAR(appointment_date), MONTH(appointment_date)", nativeQuery = true)
        List<Object[]> countMonthlyExaminations();
}