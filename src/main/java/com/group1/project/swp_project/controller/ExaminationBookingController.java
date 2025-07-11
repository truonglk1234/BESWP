package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ExaminationBookingDTO;
import com.group1.project.swp_project.dto.req.ExaminationBookingRequest;
import com.group1.project.swp_project.dto.req.ExaminationResultRequest;
import com.group1.project.swp_project.dto.res.ExaminationBookingDetailRes;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationResult;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.service.ExaminationService;
import com.group1.project.swp_project.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Đặt lịch xét nghiệm")
@RestController
@RequestMapping("api/examinations")
public class ExaminationBookingController {

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody @NotNull ExaminationBookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        System.out.println("✅ Đang booking với user email = " + email + ", ID = " + user.getId());

        // ✅ Truyền user thay vì userId
        ExaminationBooking booking = examinationService.createBooking(user, request);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long bookingId, @RequestParam String newStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found for email: " + email));

        // Truyền thẳng ID kiểu Long, không cần ép kiểu
        ExaminationBooking updated = examinationService.updateBookingStatus(bookingId, newStatus, (long) staff.getId());
        return ResponseEntity.ok(updated);
    }

//    @GetMapping("/my-bookings")
//    public ResponseEntity<List<ExaminationBooking>> getCurrentUserBookings() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
//
//        // Truyền thẳng ID kiểu Long, không cần ép kiểu
//        List<ExaminationBooking> bookings = examinationService.getBookingsForUser((long) user.getId());
//        return ResponseEntity.ok(bookings);
//    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

        List<ExaminationBookingDTO> bookings = examinationService.getBookingsForUser((long) user.getId());
        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/staff/my-tasks")
    public ResponseEntity<List<ExaminationBooking>> getCurrentStaffTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found for email: " + email));

        // Truyền thẳng ID kiểu Long, không cần ép kiểu
        List<ExaminationBooking> bookings = examinationService.getBookingsForStaff((long) staff.getId());
        return ResponseEntity.ok(bookings);
    }

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

    @DeleteMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBookingAndRefund(@PathVariable Long bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExaminationBooking booking = examinationService.getBookingById(bookingId);

        if (!booking.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("Bạn không có quyền hủy booking này");
        }

        String txnRef = booking.getPayment().getTxnRef();
        String result = examinationService.refundBookingAndCancel(txnRef);
        return ResponseEntity.ok(Map.of("message", result));
    }
}