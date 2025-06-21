package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Blog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private int id;

    @Column(name = "title", length = 200, columnDefinition = "NVARCHAR(200)")
    private String title;

    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
    private String content;



    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @Column(name = "status", nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "Pending";
        }
    }

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
