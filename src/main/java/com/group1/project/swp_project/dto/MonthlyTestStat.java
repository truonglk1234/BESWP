package com.group1.project.swp_project.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTestStat {
    private String month;
    private Long count;
}
