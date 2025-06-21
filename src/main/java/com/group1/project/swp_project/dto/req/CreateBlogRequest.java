package com.group1.project.swp_project.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBlogRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @NotNull(message = "Topic ID không được để trống")
    private Integer topicId;
}
