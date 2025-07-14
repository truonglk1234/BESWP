package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ExaminationFeedbackView;
import com.group1.project.swp_project.dto.req.FeedbackRequest;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationFeedback;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationFeedbackRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private ExaminationBookingRepository bookingRepo;

    @Autowired
    private ExaminationFeedbackRepository feedbackRepo;

    @Autowired
    private UserRepository userRepository;

    public void submitFeedback(FeedbackRequest req) {
        ExaminationBooking booking = bookingRepo.findById(req.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        if (!"Đã trả kết quả".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Xét nghiệm chưa hoàn tất, không thể đánh giá");
        }

        if (feedbackRepo.findByBookingId(req.getBookingId()).isPresent()) {
            throw new IllegalStateException("Booking này đã được đánh giá rồi");
        }

        ExaminationFeedback feedback = new ExaminationFeedback();
        feedback.setBooking(booking);
        feedback.setUser(booking.getUser());
        feedback.setService(booking.getService());
        feedback.setRating(req.getRating());
        feedback.setComment(req.getComment());

        feedbackRepo.save(feedback);
    }

    public List<ExaminationFeedbackView> getAll() {
        return feedbackRepo.findAll().stream().map(this::toView).toList();
    }

    public List<ExaminationFeedbackView> getByService(Long serviceId) {
        return feedbackRepo.findByService_Id(serviceId).stream().map(this::toView).toList();
    }

    private ExaminationFeedbackView toView(ExaminationFeedback f) {
        return new ExaminationFeedbackView(
                f.getBooking().getId(), // ✅ thêm bookingId vào
                f.getUser().getProfile().getFullName(),
                f.getRating(),
                f.getComment(),
                f.getService().getName(),
                f.getCreatedAt()
        );
    }
}
