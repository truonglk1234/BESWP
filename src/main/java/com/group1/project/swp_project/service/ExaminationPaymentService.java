package com.group1.project.swp_project.service;

import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import com.group1.project.swp_project.utils.VnPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExaminationPaymentService {

    private final VnpayService vnpayService;
    private final PaymentRepository paymentRepository;
    private final ExaminationBookingRepository bookingRepository;
    private final ExaminationService examinationService;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    public String createPayment(HttpServletRequest request, Long bookingId) {
        Optional<ExaminationBooking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        ExaminationBooking booking = bookingOpt.get();
        long amount = booking.getService().getPrice();

        String orderInfo = "Thanh toán cho booking " + bookingId;
        String paymentUrl = vnpayService.createPaymentUrl(request, amount, orderInfo);

        String txnRef = Arrays.stream(paymentUrl.split("\\?")[1].split("&"))
                .filter(p -> p.startsWith("vnp_TxnRef="))
                .map(p -> p.split("=")[1])
                .findFirst()
                .orElse("");

        ExaminationPayment examinationPayment = new ExaminationPayment();
        examinationPayment.setExaminationBooking(booking);
        examinationPayment.setAmount(amount);
        examinationPayment.setPaymentMethod("VNPAY");
        examinationPayment.setPaymentStatus("Đang xử lí");
        examinationPayment.setTxnRef(txnRef);
        examinationPayment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(examinationPayment);

        return paymentUrl;
    }

    public String handleCallback(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
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

        if (!secureHash.equals(vnp_SecureHash)) {
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}";
        }

        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

        Optional<ExaminationPayment> paymentOpt = paymentRepository.findByTxnRef(vnp_TxnRef);
        if (paymentOpt.isEmpty()) {
            return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}";
        }

        ExaminationPayment payment = paymentOpt.get();
        if (!"Đang xử lí".equals(payment.getPaymentStatus())) {
            return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
        }

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

        return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
    }

    public String refund(String txnRef) {
        Optional<ExaminationPayment> paymentOpt = paymentRepository.findByTxnRef(txnRef);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy giao dịch");
        }

        ExaminationPayment payment = paymentOpt.get();
        if (!"Thành công".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Chỉ hoàn tiền cho giao dịch đã thanh toán thành công");
        }

        ExaminationBooking booking = payment.getExaminationBooking();
        if (!List.of("Đã tiếp nhận", "Đang xử lý", "Chờ thanh toán").contains(booking.getStatus())) {
            throw new RuntimeException("Lịch đã qua xử lý, không thể hoàn tiền");
        }

        if (booking.getAppointmentDate().isBefore(LocalDateTime.now().plusHours(6))) {
            throw new RuntimeException("Chỉ hoàn tiền nếu còn ít nhất 6 giờ trước giờ hẹn");
        }

        String refundUrl = vnpayService.refundPayment(txnRef, payment.getAmount(), booking.getUser().getEmail());

        payment.setPaymentStatus("Hoàn tiền");
        paymentRepository.save(payment);

        booking.setStatus("Đã hủy");
        bookingRepository.save(booking);

        return refundUrl;
    }
}
