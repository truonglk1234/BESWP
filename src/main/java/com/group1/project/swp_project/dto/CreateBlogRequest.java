package com.group1.project.swp_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBlogRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String imageUrl;
}
