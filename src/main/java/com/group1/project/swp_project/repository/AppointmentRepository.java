package com.group1.project.swp_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group1.project.swp_project.entity.Appointment;
import com.group1.project.swp_project.entity.Users;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomer(Users customer);

    List<Appointment> findByConsultant(Users consultant);
}
