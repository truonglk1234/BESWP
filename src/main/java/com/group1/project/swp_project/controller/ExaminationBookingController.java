package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.req.ExaminationBookingRequest;
import com.group1.project.swp_project.dto.req.ExaminationResultRequest;
import com.group1.project.swp_project.dto.res.ExaminationBookingDetailRes;
import com.group1.project.swp_project.entity.*;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.service.ExaminationService;
import com.group1.project.swp_project.service.TestBookingService;
import com.group1.project.swp_project.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Đặt lịch xét nghiệm")
@RestController
@RequestMapping("api/examinations")
public class ExaminationBookingController {
    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private UserRepository userRepository;


    // Đặt lịch
    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody @NotNull ExaminationBookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users user = userRepository.findByEmail(email).orElseThrow();

        ExaminationBooking booking = examinationService.createBooking(
                (long) user.getId(), request
        );

        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{bookingId}/assign")
    public ResponseEntity<?> assignStaffToBooking(@PathVariable Long bookingId) {
        ExaminationBooking assigned = examinationService.assignStaffToBookingRoundRobin(bookingId);
        return ResponseEntity.ok(assigned);
    }

    // Staff cập nhật trạng thái booking
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long bookingId,
            @RequestParam String newStatus,
            @RequestParam Long staffId
    ) {
        ExaminationBooking updated = examinationService.updateBookingStatus(bookingId, newStatus, staffId);
        return ResponseEntity.ok(updated);
    }

    // Lấy lịch sử booking của user
    @GetMapping("/user/{userId}/bookings")
    public List<ExaminationBooking> getUserBookings(@PathVariable Long userId) {
        return examinationService.getBookingsForUser(userId);
    }

    // Lấy lịch sử booking của staff
    @GetMapping("/staff/{staffId}/bookings")
    public List<ExaminationBooking> getStaffBookings(@PathVariable Long staffId) {
        return examinationService.getBookingsForStaff(staffId);
    }

    // Staff cập nhật kết quả xét nghiệm
    @PutMapping("/{bookingId}/result")
    public ResponseEntity<?> updateResult(
            @PathVariable Long bookingId,
            @RequestBody ExaminationResultRequest resultRequest
    ) {
        ExaminationResult updated = examinationService.updateResult(
                bookingId,
                resultRequest.getResult(),
                resultRequest.getAdvice()
        );
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingDetail(@PathVariable Long id) {
        ExaminationBookingDetailRes res = examinationService.getBookingDetail(id);
        return ResponseEntity.ok(res);
    }

}
