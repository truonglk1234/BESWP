package com.group1.project.swp_project.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
public class BlogDetail {
    private int id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private String  topicName;
    private String status;
}
