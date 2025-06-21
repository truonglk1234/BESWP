package com.group1.project.swp_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consultants")
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    // Liên kết tài khoản hệ thống (nếu cần)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;
}
