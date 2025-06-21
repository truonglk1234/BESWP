package com.group1.project.swp_project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class BlogSummary {
    private int id;
    private String title;
    private LocalDateTime createdAt;
    private String authorName;
    private String topicName;
    private String status;

}
