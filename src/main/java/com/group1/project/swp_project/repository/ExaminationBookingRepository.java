package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExaminationBookingRepository extends JpaRepository<ExaminationBooking, Long> {
        List<ExaminationBooking> findByUserId(Long userId);

        List<ExaminationBooking> findByAssignedStaff_Id(Long staffId);

        List<ExaminationBooking> findAll();

        Optional<ExaminationBooking> findTopByAssignedStaffIsNotNullOrderByIdDesc();

        @Query("SELECT b.assignedStaff.id FROM ExaminationBooking b WHERE b.assignedStaff IS NOT NULL ORDER BY b.id DESC")
        List<Long> findLastAssignedStaffId(Pageable pageable);

        @Query("SELECT e FROM ExaminationBooking e " +
                        "JOIN FETCH e.service s " +
                        "WHERE e.id = :id")
        Optional<ExaminationBooking> findDetailedById(@Param("id") Long id);

}
