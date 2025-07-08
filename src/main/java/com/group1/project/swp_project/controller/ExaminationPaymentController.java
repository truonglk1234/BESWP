package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.config.VnPayConfig;
import com.group1.project.swp_project.dto.ExaminationPaymentDTO;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationPaymentRepository;
import com.group1.project.swp_project.service.ExaminationPaymentService;
import com.group1.project.swp_project.utils.VnPayUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Thanh toán xét nghiệm")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class ExaminationPaymentController {
    @Autowired
    private ExaminationPaymentService paymentService;
    @Autowired
    private ExaminationBookingRepository examinationBookingRepository;
    @Autowired
    private VnPayConfig vnPayConfig;
    @Autowired
    private ExaminationPaymentService examinationPaymentService;
    @Autowired
    private ExaminationPaymentRepository examinationPaymentRepository;



    @PostMapping("/create-vnpay-link")
    public ResponseEntity<String> createVnpay(@RequestParam Long bookingId, @RequestParam int amount) {
        String txnRef = UUID.randomUUID().toString();
        String url = paymentService.createVnpayPayment(bookingId, amount, txnRef);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(
            @RequestParam String vnp_TxnRef,
            @RequestParam String vnp_ResponseCode,
            @RequestParam(required = false) String vnp_BankCode,
            @RequestParam(required = false) String vnp_BankTranNo,
            @RequestParam(required = false) String vnp_PayDate,
            @RequestParam(required = false) String vnp_TransactionNo
    ) {
        paymentService.handleVnpayCallback(
                vnp_TxnRef,
                vnp_ResponseCode,
                vnp_BankCode,
                vnp_BankTranNo,
                vnp_PayDate,
                vnp_TransactionNo
        );
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/cancel")
    public ResponseEntity<?> cancelAndRefund(@RequestParam String txnRef) {
        try {
            paymentService.cancelBookingAndRefund(txnRef);
            return ResponseEntity.ok("Hủy lịch và hoàn tiền (nếu hợp lệ) thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
