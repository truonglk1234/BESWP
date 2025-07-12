package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.AppointmentDto;
import com.group1.project.swp_project.dto.BookingRequest;
import com.group1.project.swp_project.dto.PaymentDTO;
import com.group1.project.swp_project.entity.Appointment;
import com.group1.project.swp_project.entity.Payment;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.AppointmentRepository;
import com.group1.project.swp_project.repository.PaymentRepository;
import com.group1.project.swp_project.repository.ScheduleRepository;
import com.group1.project.swp_project.repository.UserRepository;

import org.springframework.stereotype.Service;

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

    // ✅ BOOK APPOINTMENT
    public AppointmentDto bookAppointment(int customerId, BookingRequest request) {
        Users customer = userRepo.findById((long) customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Users consultant = userRepo.findById(request.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        Appointment appt = new Appointment();
        appt.setCustomer(customer);
        appt.setConsultant(consultant);
        appt.setAppointmentDate(request.getAppointmentDate());
        appt.setDurationMinutes(request.getDurationMinutes());
        appt.setCustomerNotes(request.getCustomerNotes());
        appt.setStatus(Appointment.AppointmentStatus.PENDING);
        appointmentRepo.save(appt);

        return mapToDto(appt);
    }

    // ✅ CUSTOMER APPOINTMENTS
    public List<AppointmentDto> getCustomerAppointments(int customerId) {
        Users customer = userRepo.findById((long) customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<Appointment> list = appointmentRepo.findByCustomer(customer);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // ✅ CONSULTANT APPOINTMENTS
    public List<AppointmentDto> getConsultantAppointments(int consultantId) {
        Users consultant = userRepo.findById((long) consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        List<Appointment> list = appointmentRepo.findByConsultant(consultant);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // ✅ UPDATE STATUS
    public AppointmentDto updateAppointmentStatus(int appointmentId, String status) {
        Appointment appt = appointmentRepo.findById((long) appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(Appointment.AppointmentStatus.valueOf(status));
        appointmentRepo.save(appt);
        return mapToDto(appt);
    }

    // ✅ UPDATE PAYMENT STATUS
    public PaymentDTO updatePaymentStatus(int appointmentId, String paymentStatus) {
        Appointment appt = appointmentRepo.findById((long) appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (appt.getPayment() == null) {
            throw new RuntimeException("Payment not found");
        }
        Payment payment = appt.getPayment();
        payment.setPaymentStatus(paymentStatus);
        paymentRepo.save(payment);
        return mapToPaymentDto(payment);
    }

    // ✅ MAPPER DTO
    private AppointmentDto mapToDto(Appointment appt) {
        return new AppointmentDto(
                appt.getAppointmentId().intValue(),
                appt.getCustomer().getId(),
                appt.getCustomer().getProfile().getFullName(),
                appt.getCustomer().getPhone(),
                appt.getConsultant().getId(),
                appt.getConsultant().getProfile().getFullName(),
                appt.getAppointmentDate(),
                appt.getDurationMinutes(),
                appt.getStatus().name(),
                appt.getCustomerNotes(),
                appt.getConsultantNotes(),
                appt.getConsultationFee(),
                appt.getCreatedAt(),
                appt.getPayment() != null ? mapToPaymentDto(appt.getPayment()) : null);
    }

    private PaymentDTO mapToPaymentDto(Payment p) {
        return new PaymentDTO(
                p.getId(),
                p.getAppointment() != null ? p.getAppointment().getAppointmentId() : null,
                p.getAmount() != null ? Double.parseDouble(String.valueOf(p.getAmount())) : null,
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getTxnRef(),
                p.getPayDate(),
                null // Nếu có notes thì bổ sung
        );
    }
}
