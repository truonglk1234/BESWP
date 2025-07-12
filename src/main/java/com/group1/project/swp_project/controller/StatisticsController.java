package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.ExaminationTypeCountDTO;
import com.group1.project.swp_project.dto.MonthlyRevenueDTO;
import com.group1.project.swp_project.dto.MonthlyTestStat;
import com.group1.project.swp_project.dto.UserMonthlyStatDTO;
import com.group1.project.swp_project.service.StatisticsService;
import com.group1.project.swp_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserService userService;

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyTestStat>> getMonthlyTestStats() {
        return ResponseEntity.ok(statisticsService.getMonthlyTestStats());
    }


    @GetMapping("/type-count")
    public List<ExaminationTypeCountDTO> getExaminationTypeCount() {
        return statisticsService.getExaminationTypeCount();
    }

    @GetMapping("/monthly-users")
    public ResponseEntity<List<UserMonthlyStatDTO>> getMonthlyUserStats() {
        return ResponseEntity.ok(userService.getMonthlyUserStats());
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue() {
        return ResponseEntity.ok(statisticsService.getMonthlyRevenueStats());
    }
}
