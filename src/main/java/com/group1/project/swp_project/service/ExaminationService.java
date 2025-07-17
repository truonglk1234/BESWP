package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ExaminationBookingDTO;
import com.group1.project.swp_project.dto.req.ExaminationBookingRequest;
import com.group1.project.swp_project.dto.res.ExaminationBookingDetailRes;
import com.group1.project.swp_project.entity.*;
import com.group1.project.swp_project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExaminationService {
    @Autowired private ExaminationBookingRepository examinationBookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ServicePriceRepository servicePriceRepository;
    @Autowired private ExaminationResultRepository examinationResultRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private VnpayService vnpayService;
    public ExaminationBooking createBooking(Users user, ExaminationBookingRequest req) {
        ServicePrice service = servicePriceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        ExaminationBooking booking = new ExaminationBooking();
        booking.setUser(user);
        booking.setService(service);
        booking.setAppointmentDate(req.getAppointmentDate());
        booking.setName(req.getName());
        booking.setPhone(req.getPhone());
        booking.setEmail(req.getEmail());
        booking.setNote(req.getNote());
        booking.setStatus("Chờ thanh toán");

        return examinationBookingRepository.save(booking);
    }
    @Transactional
    public void processBookingAfterPayment(Long bookingId) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: " + bookingId));

        List<Users> staffList = userRepository.findAllByRole_IdOrderByIdAsc(4);
        staffList.sort(Comparator.comparingLong(Users::getId));

        if (staffList.isEmpty()) {
            throw new RuntimeException("Không có nhân viên nào để gán việc!");
        }
        long assignedCount = examinationBookingRepository.countByAssignedStaffIsNotNull();

        int nextIndex = (int) (assignedCount % staffList.size());
        Users nextStaff = staffList.get(nextIndex);

        booking.setAssignedStaff(nextStaff);
        booking.setStatus("Đã tiếp nhận");

        examinationBookingRepository.save(booking);
    }


    public ExaminationResult updateResult(Long bookingId, String result, String advice) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        ExaminationResult er = examinationResultRepository.findByBookingId(bookingId)
                .orElseGet(() -> {
                    ExaminationResult newEr = new ExaminationResult();
                    newEr.setBooking(booking);
                    return newEr;
                });

        er.setResult(result);
        er.setAdvice(advice);
        er.setStatus("Đã trả kết quả");

        booking.setStatus("Đã trả kết quả");
        examinationBookingRepository.save(booking);

        return examinationResultRepository.save(er);
    }

    public ExaminationBookingDetailRes getBookingDetail(Long id) {
        ExaminationBooking booking = examinationBookingRepository.findDetailedById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch đặt"));
        return ExaminationBookingDetailRes.builder()
                .id(booking.getId())
                .serviceName(booking.getService().getName())
                .price(booking.getService().getPrice())
                .appointmentDate(booking.getAppointmentDate())
                .name(booking.getName())
                .phone(booking.getPhone())
                .email(booking.getEmail())
                .note(booking.getNote())
                .status(booking.getStatus())
                .build();
    }


    public List<ExaminationBookingDTO> getBookingsForStaff(Long staffId) {
        List<String> viewableStatuses = List.of(
                "Đã tiếp nhận", "Đang xử lý", "Đang xét nghiệm", "Đã hoàn tất", "Đã trả kết quả"
        );

        List<ExaminationBooking> bookings = examinationBookingRepository.findByAssignedStaff_IdAndStatusIn(staffId, viewableStatuses);

        return bookings.stream().map(b -> ExaminationBookingDTO.builder()
                .id(b.getId())
                .appointmentDate(b.getAppointmentDate())
                .status(b.getStatus())
                .name(b.getName())
                .phone(b.getPhone())
                .email(b.getEmail())
                .note(b.getNote())
                .serviceName(b.getService().getName())
                .build()
        ).toList();
    }

    public ExaminationBooking updateBookingStatus(Long bookingId, String newStatus, Long staffId) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch xét nghiệm"));

        String currentStatus = booking.getStatus();
        if ("Chờ thanh toán".equalsIgnoreCase(currentStatus) || "Thanh toán thất bại".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Không thể xử lý booking này vì chưa được thanh toán thành công.");
        }

        Users assigned = booking.getAssignedStaff();


        if (assigned == null || !Objects.equals(assigned.getId(), staffId)) {
            throw new RuntimeException("Bạn không phải nhân viên phụ trách xét nghiệm này");
        }

        List<String> validTransitions = List.of(
                "Đã tiếp nhận", "Đang xử lý", "Đang xét nghiệm", "Đã hoàn tất", "Đã trả kết quả"
        );
        if (!validTransitions.stream().anyMatch(s -> s.equalsIgnoreCase(newStatus.trim()))) {
            throw new RuntimeException("Trạng thái mới không hợp lệ");
        }

        booking.setStatus(newStatus);
        return examinationBookingRepository.save(booking);
    }

    public ExaminationBooking getBookingById(Long id) {
        return examinationBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public String refundBookingAndCancel(String txnRef) {
        Optional<ExaminationPayment> paymentOpt = paymentRepository.findByTxnRef(txnRef);
        if (paymentOpt.isEmpty()) throw new RuntimeException("Không tìm thấy thanh toán");

        ExaminationPayment examinationPayment = paymentOpt.get();
        if (!"Thành công".equals(examinationPayment.getPaymentStatus()))
            throw new RuntimeException("Không thể hoàn tiền do chưa thanh toán thành công");

        ExaminationBooking booking = examinationPayment.getExaminationBooking();

        if (!List.of("Chờ thanh toán", "Đã tiếp nhận").contains(booking.getStatus()))
            throw new RuntimeException("Không thể hủy do booking đã được xử lý");

        if (booking.getAppointmentDate().isBefore(LocalDateTime.now().plusHours(6)))
            throw new RuntimeException("Phải hủy ít nhất 6 giờ trước giờ hẹn");


        String response = vnpayService.refundPayment(txnRef, Long.parseLong(String.valueOf(examinationPayment.getAmount())), booking.getUser().getEmail());

        booking.setStatus("Đã hủy");
        examinationPayment.setPaymentStatus("Đã hoàn tiền");
        examinationPayment.setRefundedAt(LocalDateTime.now());
        examinationBookingRepository.save(booking);
        paymentRepository.save(examinationPayment);

        return "Hoàn tiền thành công: " + response;
    }

    public List<ExaminationBookingDTO> getBookingsForUser(Long userId) {
        List<ExaminationBooking> bookings = examinationBookingRepository.findByUserId(userId);

        return bookings.stream().map(b -> {
            String result = (b.getExaminationPayment() != null && b.getExaminationPayment().getPaymentStatus().equals("Thành công"))
                    ? "Chưa có kết quả"
                    : "-";
            return ExaminationBookingDTO.builder()
                    .id(b.getId())
                    .serviceName(b.getService().getName())
                    .price(b.getService().getPrice())
                    .appointmentDate(b.getAppointmentDate())
                    .status(b.getStatus())
                    .result(result)
                    .build();
        }).toList();
    }
}