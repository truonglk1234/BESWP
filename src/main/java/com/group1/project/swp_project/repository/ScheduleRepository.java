package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Schedule;
import com.group1.project.swp_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByConsultant(Users consultant);
}
