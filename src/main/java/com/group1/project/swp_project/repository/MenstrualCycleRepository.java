package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.MenstrualCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Integer> {
    Optional<MenstrualCycle> findByUserIdAndCycleStartDate(Integer userId, LocalDate cycleStartDate);
    List<MenstrualCycle> findByUserIdOrderByCycleStartDateDesc(Integer userId);
}
