package com.group1.project.swp_project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.req.CreateAppointmentRequest;
import com.group1.project.swp_project.entity.Appointment;
import com.group1.project.swp_project.entity.Consultant;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.AppointmentRepository;
import com.group1.project.swp_project.repository.ConsultantRepository;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ConsultantRepository consultantRepository;

    public AppointmentService(AppointmentRepository repo, ConsultantRepository consultantRepo) {
        this.appointmentRepository = repo;
        this.consultantRepository = consultantRepo;
    }

    public Appointment createAppointment(Users user, CreateAppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNote(request.getNote());
        appointment.setStatus("pending");

        if (request.getConsultantId() != null) {
            Consultant consultant = consultantRepository.findById(request.getConsultantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tư vấn viên"));
            appointment.setConsultant(consultant);
        }

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByUser(Users user) {
        return appointmentRepository.findByUser(user);
    }
}
