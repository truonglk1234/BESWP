package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.MonthlyTestStat;
import com.group1.project.swp_project.service.StatisticsService;
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

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyTestStat>> getMonthlyTestStats() {
        return ResponseEntity.ok(statisticsService.getMonthlyTestStats());
    }
}
