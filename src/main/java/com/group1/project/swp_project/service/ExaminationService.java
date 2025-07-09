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
import java.util.Random;

@Service
public class ExaminationService {
    @Autowired
    private ExaminationBookingRepository examinationBookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServicePriceRepository servicePriceRepository;

    @Autowired
    private ExaminationResultRepository examinationResultRepository;

    public ExaminationBooking createBooking(Long userId, ExaminationBookingRequest req) {
        Users user = userRepository.findById(userId).orElseThrow();
        ServicePrice service = servicePriceRepository.findById(req.getServiceId()).orElseThrow();

        ExaminationBooking booking = new ExaminationBooking();
        booking.setUser(user);
        booking.setService(service);
        booking.setAppointmentDate(req.getAppointmentDate());
        booking.setName(req.getName());
        booking.setPhone(req.getPhone());
        booking.setEmail(req.getEmail());
        booking.setNote(req.getNote());
        booking.setStatus("Đã tiếp nhận");

        examinationBookingRepository.save(booking);

        return booking;
    }



    public List<ExaminationBooking> getBookingsForUser(Long userId) {
        return examinationBookingRepository.findByUserId(userId);
    }

    public List<ExaminationBooking> getBookingsForStaff(Long staffId) {
        return examinationBookingRepository.findByAssignedStaff_Id(staffId);
    }

    public ExaminationBooking updateBookingStatus(Long bookingId, String newStatus, Long staffId) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch xét nghiệm"));

        Users assigned = booking.getAssignedStaff();
        if (assigned == null || assigned.getId() != staffId) {
            throw new RuntimeException("Không phải nhân viên phụ trách xét nghiệm này");
        }

        List<String> validTransitions = List.of(
                "Đã tiếp nhận",        // RECEIVED
                "Đang xử lý",          // NEW STATE
                "Đang xét nghiệm",     // IN_PROGRESS
                "Đã hoàn tất",         // DONE
                "Đã trả kết quả"       // RESULT_RETURNED
        );
        if (!validTransitions.contains(newStatus)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        booking.setStatus(newStatus);
        return examinationBookingRepository.save(booking);
    }

    public ExaminationResult updateResult(Long bookingId, String result, String advice) {
        // 1. Lấy booking
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // 2. Lấy (hoặc tạo mới) kết quả xét nghiệm
        ExaminationResult er = examinationResultRepository.findByBookingId(bookingId)
                .orElseGet(() -> {
                    ExaminationResult newEr = new ExaminationResult();
                    newEr.setBooking(booking);
                    return newEr;
                });

        // 3. Gán kết quả và lời khuyên
        er.setResult(result);
        er.setAdvice(advice);
        er.setStatus("Đã trả kết quả");

        // 4. Cập nhật trạng thái booking
        booking.setStatus("Đã trả kết quả");
        examinationBookingRepository.save(booking);

        // 5. Lưu và trả về kết quả
        return examinationResultRepository.save(er);
    }


    public ExaminationBooking assignStaffToBookingRoundRobin(Long bookingId) {
        ExaminationBooking booking = examinationBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // 1. Lấy danh sách staff (role_id = 4), sort theo user_id tăng dần
        List<Users> staffList = userRepository.findAllByRole_IdOrderByIdAsc(4);
        if (staffList.isEmpty()) throw new RuntimeException("Không có staff nào!");

        // 2. Lấy staff cuối cùng vừa được assign booking (nếu có)
        Long lastStaffId = null;
        var lastAssignedBookingOpt = examinationBookingRepository.findTopByAssignedStaffIsNotNullOrderByIdDesc();
        if (lastAssignedBookingOpt.isPresent()) {
            lastStaffId = (long) lastAssignedBookingOpt.get().getAssignedStaff().getId();
        }

        // 3. Tìm staff kế tiếp (round-robin)
        Users nextStaff;
        if (lastStaffId == null) {
            nextStaff = staffList.get(0);
        } else {
            int idx = -1;
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.get(i).getId() == lastStaffId) {
                    idx = i;
                    break;
                }
            }
            idx = (idx + 1) % staffList.size();
            nextStaff = staffList.get(idx);
        }

        // 4. Assign staff cho booking này
        booking.setAssignedStaff(nextStaff);
        booking.setStatus("Đã tiếp nhận");
        return examinationBookingRepository.save(booking);
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
}
