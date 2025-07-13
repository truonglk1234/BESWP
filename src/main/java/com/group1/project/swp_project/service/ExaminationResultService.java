package com.group1.project.swp_project.service;


import com.group1.project.swp_project.dto.ExaminationResultResponse;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.entity.ExaminationResult;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import com.group1.project.swp_project.repository.ExaminationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExaminationResultService {
    @Autowired
    private  ExaminationBookingRepository bookingRepo;
    @Autowired
    private  ExaminationResultRepository resultRepo;

    public ExaminationResultResponse getResultForCustomer(Long bookingId, String email) {

        ExaminationBooking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch xét nghiệm"));

        ExaminationResult result = resultRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chưa có kết quả cho lịch này"));

        return ExaminationResultResponse.builder()
                .serviceName(booking.getService().getName())
                .appointmentDate(booking.getAppointmentDate())
                .status(result.getStatus())
                .result(result.getResult())
                .advice(result.getAdvice())
                .build();
    }
}
