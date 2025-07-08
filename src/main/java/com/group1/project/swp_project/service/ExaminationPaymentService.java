package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ExaminationPaymentDTO;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationPaymentRepository;
import com.group1.project.swp_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ExaminationPaymentService {
    private final ExaminationPaymentRepository paymentRepo;
    private final ExaminationBookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final VnpayRefundService vnpayRefundService;


    public String createVnpayPayment(Long bookingId, int amount, String txnRef) {
        ExaminationBooking booking = bookingRepo.findById(bookingId).orElseThrow();
        ExaminationPayment payment = ExaminationPayment.builder()
                .examinationBooking(booking)
                .amount(amount)
                .paymentStatus("PENDING")
                .txnRef(txnRef)
                .paymentMethod("VNPAY")
                .build();
        paymentRepo.save(payment);
        // TODO: Tạo URL thanh toán VNPAY
        String vnpayUrl = "https://sandbox.vnpayment.vn/payment/" + txnRef;
        return vnpayUrl;
    }

    // Callback từ VNPAY về (controller sẽ gọi hàm này)
    @Transactional
    public void handleVnpayCallback(String txnRef, String vnp_ResponseCode) {
        ExaminationPayment payment = paymentRepo.findByTxnRef(txnRef).orElseThrow();
        ExaminationBooking booking = payment.getExaminationBooking();

        if ("00".equals(vnp_ResponseCode)) { // thanh toán thành công
            payment.setPaymentStatus("SUCCESS");
            paymentRepo.save(payment);

            booking.setStatus("WAITING_ASSIGN");
            bookingRepo.save(booking);

            assignStaffRoundRobin(booking.getId());
        } else {
            payment.setPaymentStatus("FAILED");
            paymentRepo.save(payment);
        }
    }

    // Luân phiên staff từ id nhỏ đến lớn
    @Transactional
    public void assignStaffRoundRobin(Long bookingId) {
        List<Users> staffs = userRepo.findAllStaffOrderByIdAsc();
        Long lastStaffId = null;
        List<Long> lastList = bookingRepo.findLastAssignedStaffId((Pageable) PageRequest.of(0,1));
        if (!lastList.isEmpty()) lastStaffId = lastList.get(0);

        int idx = 0;
        if (lastStaffId != null) {
            for (int i = 0; i < staffs.size(); i++) {
                if (Objects.equals(staffs.get(i).getId(), lastStaffId)) {
                    idx = (i+1) % staffs.size();
                    break;
                }
            }
        }
        Users staffToAssign = staffs.get(idx);
        ExaminationBooking booking = bookingRepo.findById(bookingId).orElseThrow();
        booking.setAssignedStaff(staffToAssign);
        booking.setStatus("RECEIVED");
        bookingRepo.save(booking);
    }

    @Transactional
    public void cancelBookingAndRefund(String txnRef) {
        ExaminationPayment payment = paymentRepo.findByTxnRef(txnRef).orElseThrow();
        ExaminationBooking booking = payment.getExaminationBooking();


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentTime = booking.getAppointmentDate();


        if (now.isAfter(appointmentTime.minusHours(12))) {
            throw new RuntimeException("Bạn chỉ có thể hoàn tiền trước 12 tiếng so với giờ hẹn.");
        }


        String status = booking.getStatus().toLowerCase();
        if (status.equals("đang xử lý") || status.equals("đang xét nghiệm")
                || status.equals("đã hoàn tất") || status.equals("đã trả kết quả")) {
            throw new RuntimeException("Không thể hoàn tiền khi xét nghiệm đã bắt đầu xử lý.");
        }

        // ✅ Trường hợp hợp lệ → hoàn tiền
        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            boolean refundOK = vnpayRefundService.refund(payment);
            if (refundOK) {
                payment.setPaymentStatus("REFUNDED");
                booking.setStatus("đã hủy");
            } else {
                throw new RuntimeException("Hoàn tiền thất bại từ VNPAY");
            }
        } else if ("PENDING".equals(payment.getPaymentStatus())) {
            payment.setPaymentStatus("CANCELED");
            booking.setStatus("đã hủy");
        } else {
            throw new RuntimeException("Không thể huỷ với trạng thái thanh toán hiện tại");
        }

        paymentRepo.save(payment);
        bookingRepo.save(booking);
    }
}
