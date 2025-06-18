package com.group1.project.swp_project.dto;

import com.group1.project.swp_project.entity.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummary {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String rollName;

    public static UserSummary fromEntity(Users user) {
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getProfile() != null ? user.getProfile().getFullName() : "Chưa có Profile")
                .email(user.getEmail())
                .phone(user.getPhone())
                .rollName(user.getRole() != null ? user.getRole().getRoleName() : "Chưa có Role")
                .build();
    }

}
