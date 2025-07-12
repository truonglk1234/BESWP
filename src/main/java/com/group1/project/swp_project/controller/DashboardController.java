package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.DashboardSummary;
import com.group1.project.swp_project.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Dashboard & Report.")
@RestController
@RequestMapping("/api/admin")
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard-summary")
    public DashboardSummary getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }
}
