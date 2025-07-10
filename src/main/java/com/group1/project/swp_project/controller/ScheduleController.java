package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ScheduleDTO;
import com.group1.project.swp_project.entity.Schedule;
import com.group1.project.swp_project.service.ScheduleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleDTO dto) {
        try {
            Schedule created = scheduleService.createSchedule(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            // Có thể custom Exception sau này
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<List<Schedule>> getSchedulesByConsultant(@PathVariable int consultantId) {
        List<Schedule> schedules = scheduleService.getSchedulesByConsultant(consultantId);
        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schedules);
    }
}
