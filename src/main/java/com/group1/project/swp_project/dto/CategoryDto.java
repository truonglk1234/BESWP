package com.group1.project.swp_project.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {
    private String name;
    private String type;
}
