package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user; // Đổi Users thành entity user của bạn
    @Column(name = "type", columnDefinition = "nvarchar(200)")
    private String type;
    @Column(name = "content", columnDefinition = "nvarchar(200)")
    private String content;
    private Boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String link;
}
