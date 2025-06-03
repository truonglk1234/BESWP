package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="[User]")


public class User {

    //Đánh dấu trường id là khóa chính (primary key) của thực thể (entity).
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Ánh xạ thuộc tính id với cột user_id) trong bảng cơ sở dữ liệu.
    @Column(name = "user_id")
    private int id;

    @Column(name = "userphone", length = 50, nullable = true)
    private String userPhone;

    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "email", length = 100)
    private String email;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private UserStatus status;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}