package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ExaminationBookingView;
import com.group1.project.swp_project.entity.ExaminationBooking;
import com.group1.project.swp_project.repository.ExaminationBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExaminationManagerService {
    @Autowired
    private ExaminationBookingRepository repo;

    public List<ExaminationBookingView> getAllBookings() {
        return repo.findAllByOrderByAppointmentDateDesc().stream().map(b -> {
            ExaminationBookingView view = new ExaminationBookingView();
            view.setId(b.getId());
            view.setUserFullName(b.getUser().getProfile().getFullName());
            view.setServiceName(b.getService().getName());
            view.setAppointmentDate(b.getAppointmentDate());
            view.setStatus(b.getStatus());
            return view;
        }).collect(Collectors.toList());
    }
}
