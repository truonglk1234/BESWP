package com.group1.project.swp_project.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardSummary {

    private long totalUsers;
    private long customers;
    private long consultants;
    private long staff;
    private long newAccountsThisMonth;

    private long publishedBlogsThisMonth;

    private long examinationCountThisMonth;

    private BigDecimal totalIncomeThisMonth;
}
