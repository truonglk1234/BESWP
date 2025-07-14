package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ExaminationFeedbackView;
import com.group1.project.swp_project.dto.req.FeedbackRequest;
import com.group1.project.swp_project.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Đánh giá dịch vụ xét nghiệm")
@RestController
@RequestMapping("/api/feedbacks")
public class ExaminationFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest req) {
        feedbackService.submitFeedback(req);
        return ResponseEntity.ok(Map.of("message", "Đánh giá thành công"));
    }

    @GetMapping
    public List<ExaminationFeedbackView> getAllFeedbacks() {
        return feedbackService.getAll();
    }

    // ✅ Trả về danh sách bookingId đã đánh giá (dành cho FE để ẩn form đã gửi)
    @GetMapping("/booking-ids")
    public List<Long> getAllReviewedBookingIds() {
        return feedbackService.getAll()
                .stream()
                .map(ExaminationFeedbackView::getBookingId)
                .toList();
    }
}
