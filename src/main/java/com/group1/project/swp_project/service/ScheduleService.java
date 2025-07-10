package com.group1.project.swp_project.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.ScheduleDTO;
import com.group1.project.swp_project.entity.Schedule;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.ScheduleRepository;
import com.group1.project.swp_project.repository.UserRepository;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository usersRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, UserRepository usersRepository) {
        this.scheduleRepository = scheduleRepository;
        this.usersRepository = usersRepository;
    }

    public Schedule createSchedule(ScheduleDTO dto) {
        Users consultant = usersRepository.findById(dto.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        Schedule s = Schedule.builder()
                .type(dto.getType())
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(LocalTime.parse(dto.getStartTime()))
                .endTime(LocalTime.parse(dto.getEndTime()))
                .price(dto.getPrice())
                .durationMinutes(dto.getDurationMinutes())
                .note(dto.getNote())
                .isAvailable(dto.getIsAvailable())
                .consultant(consultant)
                .build();

        return scheduleRepository.save(s);
    }

    public List<Schedule> getSchedulesByConsultant(int consultantId) {
        Users consultant = usersRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        return scheduleRepository.findByConsultant(consultant);
    }
}
