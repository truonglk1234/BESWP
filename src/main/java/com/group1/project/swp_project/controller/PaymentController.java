package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationPaymentRepository;
import com.group1.project.swp_project.service.VnpayService;
import com.group1.project.swp_project.utils.VnPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VnpayService vnpayService;
    private final ExaminationPaymentRepository paymentRepository;
    private final ExaminationBookingRepository bookingRepository;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @PostMapping("/create-payment")

    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam Long bookingId) {

        Optional<ExaminationBooking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return new ResponseEntity<>("Booking with ID " + bookingId + " not found", HttpStatus.NOT_FOUND);
        }
        ExaminationBooking booking = bookingOpt.get();


        long amount;
        try {

            amount = booking.getService().getPrice();
        } catch (Exception e) {

            return new ResponseEntity<>("Could not determine the price for this booking. Associated service may be missing.", HttpStatus.BAD_REQUEST);
        }


        String orderInfo = "Thanh toán cho booking " + bookingId;
        String paymentUrl = vnpayService.createPaymentUrl(request, amount, orderInfo);


        String txnRef = "";
        try {
            String[] params = paymentUrl.substring(paymentUrl.indexOf("?") + 1).split("&");
            for (String param : params) {
                if (param.startsWith("vnp_TxnRef=")) {
                    txnRef = param.substring("vnp_TxnRef=".length());
                    break;
                }
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing payment URL", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        ExaminationPayment payment = new ExaminationPayment();
        payment.setExaminationBooking(booking);
        payment.setAmount(String.valueOf(amount));
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
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");


        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            hashData.append(fieldName);
            hashData.append('=');
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
            if(itr.hasNext()) {
                hashData.append('&');
            }
        }

        String secureHash = VnPayUtils.hmacSHA512(this.vnp_HashSecret, hashData.toString());

        if (secureHash.equals(vnp_SecureHash)) {
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");

            Optional<ExaminationPayment> paymentOpt = paymentRepository.findByTxnRef(vnp_TxnRef);
            if (paymentOpt.isPresent()) {
                ExaminationPayment payment = paymentOpt.get();
                if ("Đang xử lí".equals(payment.getPaymentStatus())) {
                    if ("00".equals(vnp_ResponseCode)) {
                        payment.setPaymentStatus("Thành công");
                    } else {
                        payment.setPaymentStatus("Thật bại");
                    }
                    payment.setPayDate(LocalDateTime.now());
                    payment.setBankCode(request.getParameter("vnp_BankCode"));
                    payment.setBankTranNo(request.getParameter("vnp_BankTranNo"));
                    payment.setVnpTransactionNo(request.getParameter("vnp_TransactionNo"));
                    paymentRepository.save(payment);
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
}