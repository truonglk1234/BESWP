package com.group1.project.swp_project.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantDTO {
    private String fullName;
    private String specialty;
    private String avatarUrl;
    private Boolean gender;
    private Integer experienceYears;
    private String description;
    private String email;
    private String phone;
}
