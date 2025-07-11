package com.group1.project.swp_project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.ScheduleDTO;
import com.group1.project.swp_project.dto.ScheduleSlotDto;
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
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .price(dto.getPrice())
                .durationMinutes(dto.getDurationMinutes())
                .note(dto.getNote())
                .isAvailable(dto.getIsAvailable())
                .consultant(consultant)
                .build();

        return scheduleRepository.save(s);
    }

    // ✅ Sửa trả ra DTO gắn ngày
    public List<ScheduleSlotDto> getAvailableSchedulesByDate(int consultantId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        java.time.DayOfWeek dow = localDate.getDayOfWeek();
        Schedule.DayOfWeek targetDow = switch (dow) {
            case MONDAY -> Schedule.DayOfWeek.THU_HAI;
            case TUESDAY -> Schedule.DayOfWeek.THU_BA;
            case WEDNESDAY -> Schedule.DayOfWeek.THU_TU;
            case THURSDAY -> Schedule.DayOfWeek.THU_NAM;
            case FRIDAY -> Schedule.DayOfWeek.THU_SAU;
            case SATURDAY -> Schedule.DayOfWeek.THU_BAY;
            case SUNDAY -> Schedule.DayOfWeek.CHU_NHAT;
        };

        return scheduleRepository.findAvailableSchedulesByDayOfWeek(consultantId, targetDow)
                .stream()
                .map(s -> new ScheduleSlotDto(
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getPrice(),
                        s.getDurationMinutes(),
                        s.getIsAvailable(),
                        s.getNote()))
                .toList();
    }

    public List<Schedule> getSchedulesByConsultant(int consultantId) {
        return scheduleRepository.findByConsultantIdAndIsAvailableTrue(consultantId);
    }
}
