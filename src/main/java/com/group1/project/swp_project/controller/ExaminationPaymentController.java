package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import com.group1.project.swp_project.service.ExaminationPaymentService;
import com.group1.project.swp_project.service.ExaminationService;
import com.group1.project.swp_project.service.VnpayService;
import com.group1.project.swp_project.utils.VnPayUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "Thanh toán dịch vụ khám chữa bệnh")
@RestController
@RequestMapping("/api/v1/examinationPayment")
@RequiredArgsConstructor
public class ExaminationPaymentController {

    private final ExaminationPaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam Long bookingId) {
        try {
            String paymentUrl = paymentService.createPayment(request, bookingId);
            return ResponseEntity.ok(paymentUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(HttpServletRequest request) {
        String result = paymentService.handleCallback(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vnpay-callback-redirect")
    public void vnpayCallbackRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = request.getParameter("vnp_ResponseCode");
        paymentService.handleCallback(request);
        if ("00".equals(code)) {
            response.sendRedirect("http://localhost:5173/tests");
        } else {
            response.sendRedirect("http://localhost:5173/");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refund(@RequestParam String txnRef) {
        try {
            String refundUrl = paymentService.refund(txnRef);
            return ResponseEntity.ok("Yêu cầu hoàn tiền đã được gửi: " + refundUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

