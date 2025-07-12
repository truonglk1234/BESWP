package com.group1.project.swp_project.dto;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExaminationTypeCountDTO {
    private String serviceName;
    private Long count;
}
