package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.ExaminationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExaminationFeedbackRepository extends JpaRepository<ExaminationFeedback, Long> {
    Optional<ExaminationFeedback> findByBookingId(Long bookingId);

    List<ExaminationFeedback> findAll();

    List<ExaminationFeedback> findByService_Id(Long serviceId);

    @Query("SELECT AVG(e.rating) FROM ExaminationFeedback e WHERE e.consultant.id = :consultantId")
    Double getAverageRatingByConsultantId(@Param("consultantId") int consultantId);
}