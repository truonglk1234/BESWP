package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.Payment;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
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
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VnpayService vnpayService;
    private final PaymentRepository paymentRepository;
    private final ExaminationBookingRepository bookingRepository;
    private final ExaminationService examinationService;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam Long bookingId) {
        Optional<ExaminationBooking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return new ResponseEntity<>("Booking not found", HttpStatus.NOT_FOUND);
        }

        ExaminationBooking booking = bookingOpt.get();
        long amount;
        try {
            amount = booking.getService().getPrice();
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot determine price", HttpStatus.BAD_REQUEST);
        }

        String orderInfo = "Thanh toán cho booking " + bookingId;
        String paymentUrl = vnpayService.createPaymentUrl(request, amount, orderInfo);

        String txnRef = Arrays.stream(paymentUrl.split("\\?")[1].split("&"))
                .filter(p -> p.startsWith("vnp_TxnRef="))
                .map(p -> p.split("=")[1])
                .findFirst()
                .orElse("");

        Payment payment = new Payment();
        payment.setExaminationBooking(booking);
        payment.setAmount(Long.valueOf(String.valueOf(amount)));
        payment.setPaymentMethod("VNPAY");
        payment.setPaymentStatus("Đang xử lí");
        payment.setTxnRef(txnRef);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String key = fieldNames.get(i);
            String value = fields.get(key);
            hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
            }
        }

        String secureHash = VnPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());

        if (secureHash.equals(vnp_SecureHash)) {
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

            Optional<Payment> paymentOpt = paymentRepository.findByTxnRef(vnp_TxnRef);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                if ("Đang xử lí".equals(payment.getPaymentStatus())) {
                    if ("00".equals(vnp_ResponseCode)) {
                        payment.setPaymentStatus("Thành công");
                        payment.setPayDate(LocalDateTime.now());
                        payment.setBankCode(request.getParameter("vnp_BankCode"));
                        payment.setBankTranNo(request.getParameter("vnp_BankTranNo"));
                        payment.setVnpTransactionNo(request.getParameter("vnp_TransactionNo"));
                        paymentRepository.save(payment);

                        examinationService.processBookingAfterPayment(payment.getExaminationBooking().getId());
                    } else {
                        payment.setPaymentStatus("Thất bại");
                        payment.setPayDate(LocalDateTime.now());
                        paymentRepository.save(payment);
                    }
                    return new ResponseEntity<>("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}", HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("{\"RspCode\":\"01\",\"Message\":\"Order not found\"}", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}", HttpStatus.OK);
        }
    }

    @GetMapping("/vnpay-callback-redirect")
    public void vnpayCallbackRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            vnpayCallback(request);
            response.sendRedirect("http://localhost:5173/tests");
        } else {
            response.sendRedirect("http://localhost:5173/");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refund(@RequestParam String txnRef) {
        Optional<Payment> paymentOpt = paymentRepository.findByTxnRef(txnRef);
        if (paymentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy giao dịch");
        }

        Payment payment = paymentOpt.get();
        if (!"Thành công".equals(payment.getPaymentStatus())) {
            return ResponseEntity.badRequest().body("Chỉ hoàn tiền cho giao dịch đã thanh toán thành công");
        }

        ExaminationBooking booking = payment.getExaminationBooking();
        if (!List.of("Đã tiếp nhận", "Đang xử lý", "Chờ thanh toán").contains(booking.getStatus())) {
            return ResponseEntity.badRequest().body("Lịch đã qua xử lý, không thể hoàn tiền");
        }

        if (booking.getAppointmentDate().isBefore(LocalDateTime.now().plusHours(6))) {
            return ResponseEntity.badRequest().body("Chỉ hoàn tiền nếu còn ít nhất 6 giờ trước giờ hẹn");
        }

        // ✅ Gọi refund VNPay thực tế
        String refundUrl = vnpayService.refundPayment(txnRef, Long.parseLong(String.valueOf(payment.getAmount())), booking.getUser().getEmail());

        // ✅ Cập nhật trạng thái sau khi tạo URL (tùy backend của bạn xử lý tiếp ra sao)
        payment.setPaymentStatus("Hoàn tiền");
        paymentRepository.save(payment);

        booking.setStatus("Đã hủy");
        bookingRepository.save(booking);

        return ResponseEntity.ok("Yêu cầu hoàn tiền đã được gửi: " + refundUrl);
    }
}
