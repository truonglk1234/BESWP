package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExaminationResultRepository extends JpaRepository<ExaminationResult, Long> {
    Optional<ExaminationResult> findByBookingId(Long bookingId);
    ExaminationResult save(ExaminationResult entity);
}
