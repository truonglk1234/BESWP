package com.group1.project.swp_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDTO {
    private String monthLabel;
    private Long totalRevenue;
}
