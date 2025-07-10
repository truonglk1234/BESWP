package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.req.ExaminationBookingRequest;
import com.group1.project.swp_project.dto.res.ExaminationBookingDetailRes;
import com.group1.project.swp_project.entity.*;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationResultRepository;
import com.group1.project.swp_project.repository.ServicePriceRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class ExaminationService {
    @Autowired private ExaminationBookingRepository examinationBookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ServicePriceRepository servicePriceRepository;
    @Autowired private ExaminationResultRepository examinationResultRepository;

    public ExaminationBooking createBooking(Long userId, ExaminationBookingRequest req) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ServicePrice service = servicePriceRepository.findById(req.getServiceId()).orElseThrow(() -> new RuntimeException("Service not found"));

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
        if (staffList.isEmpty()) {
            throw new RuntimeException("Không có nhân viên nào để gán việc!");
        }

        Users nextStaff;
        var lastAssignedOpt = examinationBookingRepository.findTopByAssignedStaffIsNotNullOrderByIdDesc();

        if (lastAssignedOpt.isEmpty() || lastAssignedOpt.get().getAssignedStaff() == null) {
            nextStaff = staffList.get(0);
        } else {
            Long lastStaffId = (long) lastAssignedOpt.get().getAssignedStaff().getId();
            int lastIdx = -1;

            // SỬA LẠI CÁCH SO SÁNH BẰNG Objects.equals()
            for (int i = 0; i < staffList.size(); i++) {
                if (Objects.equals(staffList.get(i).getId(), lastStaffId)) {
                    lastIdx = i;
                    break;
                }
            }

            if (lastIdx == -1) {
                nextStaff = staffList.get(0);
            } else {
                nextStaff = staffList.get((lastIdx + 1) % staffList.size());
            }
        }

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


    public List<ExaminationBooking> getBookingsForUser(Long userId) {
        return examinationBookingRepository.findByUserId(userId);
    }

    public List<ExaminationBooking> getBookingsForStaff(Long staffId) {
        List<String> viewableStatuses = List.of(
                "Đã tiếp nhận", "Đang xử lý", "Đang xét nghiệm", "Đã hoàn tất", "Đã trả kết quả"
        );
        return examinationBookingRepository.findByAssignedStaff_IdAndStatusIn(staffId, viewableStatuses);
    }

    public ExaminationBooking updateBookingStatus(Long bookingId, String newStatus, Long staffId) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch xét nghiệm"));

        String currentStatus = booking.getStatus();
        if ("Chờ thanh toán".equalsIgnoreCase(currentStatus) || "Thanh toán thất bại".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Không thể xử lý booking này vì chưa được thanh toán thành công.");
        }

        Users assigned = booking.getAssignedStaff();

        // SỬA LẠI CÁCH SO SÁNH BẰNG Objects.equals()
        if (assigned == null || !Objects.equals(assigned.getId(), staffId)) {
            throw new RuntimeException("Bạn không phải nhân viên phụ trách xét nghiệm này");
        }

        List<String> validTransitions = List.of(
                "Đã tiếp nhận", "Đang xử lý", "Đang xét nghiệm", "Đã hoàn tất", "Đã trả kết quả"
        );
        if (!validTransitions.contains(newStatus)) {
            throw new RuntimeException("Trạng thái mới không hợp lệ");
        }

        booking.setStatus(newStatus);
        return examinationBookingRepository.save(booking);
    }
}