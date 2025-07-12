package com.group1.project.swp_project.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMonthlyStatDTO {
    private String month;
    private Long count;
}
