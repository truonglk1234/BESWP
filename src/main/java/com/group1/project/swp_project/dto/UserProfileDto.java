package com.group1.project.swp_project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private String fullName;
    private Boolean gender;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
    private String address;
    private String avatarUrl;

    private String role;
    private LocalDateTime createdAt;
    private boolean active;
}
