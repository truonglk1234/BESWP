package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quản lí xét nghiệm của Staff")
@RestController
@RequestMapping("/api/staff/bookings")
public class StaffExaminationBookingController {

    @Autowired
    private ExaminationBookingRepository bookingRepo;
    @Autowired
    private ExaminationBookingRepository examinationBookingRepository;


    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {

        List<String> validTransitions = List.of("RECEIVED", "IN_PROGRESS", "DONE", "RESULT_RETURNED");
        if (!validTransitions.contains(status)) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ");
        }

        // B2: Tìm booking
        ExaminationBooking booking = examinationBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // B3: Cập nhật trạng thái
        booking.setStatus(status);
        examinationBookingRepository.save(booking);

        return ResponseEntity.ok("Đã cập nhật trạng thái thành: " + status);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllBookings() {
        List<ExaminationBooking> all = bookingRepo.findAll();
        return ResponseEntity.ok(all);
    }
}
