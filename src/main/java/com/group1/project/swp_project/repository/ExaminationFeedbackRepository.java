package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExaminationFeedbackRepository extends JpaRepository<ExaminationFeedback, Long> {
    Optional<ExaminationFeedback> findByBookingId(Long bookingId);
    List<ExaminationFeedback> findAll();
    List<ExaminationFeedback> findByService_Id(Long serviceId);

}