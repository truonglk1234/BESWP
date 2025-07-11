package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Schedule;
import com.group1.project.swp_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByConsultant(Users consultant);

    List<Schedule> findByConsultantIdAndIsAvailableTrue(int consultantId);

    @Query("SELECT s FROM Schedule s WHERE s.consultant.id = :consultantId AND s.dayOfWeek = :dow AND s.isAvailable = true")
    List<Schedule> findAvailableSchedulesByDayOfWeek(@Param("consultantId") int consultantId,
            @Param("dow") Schedule.DayOfWeek dow);

}
