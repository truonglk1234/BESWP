package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.AppointmentDto;
import com.group1.project.swp_project.dto.BookingRequest;
import com.group1.project.swp_project.dto.PaymentDTO;
import com.group1.project.swp_project.entity.Appointment;
import com.group1.project.swp_project.entity.ExaminationPayment;
import com.group1.project.swp_project.entity.Schedule;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.AppointmentRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import com.group1.project.swp_project.repository.ScheduleRepository;
import com.group1.project.swp_project.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepo;
    private final PaymentRepository paymentRepo;
    private final ScheduleRepository scheduleRepo;
    private final UserRepository userRepo;

    public AppointmentService(AppointmentRepository appointmentRepo,
            PaymentRepository paymentRepo,
            ScheduleRepository scheduleRepo,
            UserRepository userRepo) {
        this.appointmentRepo = appointmentRepo;
        this.paymentRepo = paymentRepo;
        this.scheduleRepo = scheduleRepo;
        this.userRepo = userRepo;
    }

    public AppointmentDto bookAppointment(int customerId, BookingRequest request) {
        Users customer = userRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Users consultant = userRepo.findById(request.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        Schedule schedule = scheduleRepo.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (schedule.getPrice() == null)
            throw new RuntimeException("Schedule price is missing.");
        if (schedule.getDurationMinutes() == null)
            throw new RuntimeException("Schedule duration is missing.");

        Appointment appt = new Appointment();
        appt.setCustomer(customer);
        appt.setConsultant(consultant);
        appt.setAppointmentDate(request.getAppointmentDate());
        appt.setDurationMinutes(schedule.getDurationMinutes());
        appt.setCustomerNotes(request.getCustomerNotes());
        appt.setConsultationFee(schedule.getPrice());
        appt.setStatus(Appointment.AppointmentStatus.PENDING);

        appointmentRepo.save(appt);

        return mapToDto(appt);
    }

    public List<AppointmentDto> getCustomerAppointments(int customerId) {
        Users customer = userRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return appointmentRepo.findByCustomer(customer)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AppointmentDto> getConsultantAppointments(int consultantId) {
        Users consultant = userRepo.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        return appointmentRepo.findByConsultant(consultant)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AppointmentDto updateAppointmentStatus(int appointmentId, String status) {
        Appointment appt = appointmentRepo.findById((long) appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.AppointmentStatus.valueOf(status));
        appointmentRepo.save(appt);
        return mapToDto(appt);
    }

    public PaymentDTO updatePaymentStatus(int appointmentId, String paymentStatus) {
        Appointment appt = appointmentRepo.findById((long) appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        ExaminationPayment payment = appt.getExaminationPayment();
        if (payment == null)
            throw new RuntimeException("Payment not found");

        payment.setPaymentStatus(paymentStatus);
        paymentRepo.save(payment);
        return mapToPaymentDto(payment);
    }

    // ✅ SAFE MAP TO DTO
    private AppointmentDto mapToDto(Appointment appt) {
        String customerName = appt.getCustomer().getProfile() != null
                ? appt.getCustomer().getProfile().getFullName()
                : "Unknown";

        String consultantName = appt.getConsultant().getProfile() != null
                ? appt.getConsultant().getProfile().getFullName()
                : "Unknown";

        return new AppointmentDto(
                appt.getAppointmentId(),
                appt.getCustomer().getId(),
                customerName,
                appt.getCustomer().getPhone(),
                appt.getConsultant().getId(),
                consultantName,
                appt.getAppointmentDate(),
                appt.getDurationMinutes(),
                appt.getStatus().name(),
                appt.getCustomerNotes(),
                appt.getConsultantNotes(),
                appt.getConsultationFee(),
                appt.getCreatedAt(),
                appt.getExaminationPayment() != null ? mapToPaymentDto(appt.getExaminationPayment()) : null);
    }

    private PaymentDTO mapToPaymentDto(ExaminationPayment p) {
        return new PaymentDTO(
                p.getId(),
                p.getAppointment() != null ? p.getAppointment().getAppointmentId() : null,
                p.getAmount() != null ? p.getAmount().doubleValue() : null,
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getTxnRef(),
                p.getPayDate(),
                null // Optional: payment notes nếu cần
        );
    }
}