package com.group1.project.swp_project.dto;

import com.group1.project.swp_project.entity.Users;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantDTO {
    private Integer id;
    private String fullName;
    private String specialty;
    private String avatarUrl;
    private Boolean gender;
    private Integer experienceYears;
    private String description;
    private String email;
    private String phone;

    private Double averageRating;

    public static ConsultantDTO fromUser(Users user, Double avgRating) {
        if (user == null || user.getProfile() == null)
            return null;

        return ConsultantDTO.builder()
                .id(user.getId())
                .fullName(user.getProfile().getFullName())
                .specialty(user.getProfile().getSpecialty())
                .avatarUrl(user.getProfile().getAvatarUrl())
                .gender(user.getProfile().getGender())
                .experienceYears(user.getProfile().getExperienceYears())
                .description(user.getProfile().getDescription())
                .email(user.getEmail())
                .phone(user.getPhone())
                .averageRating(avgRating)
                .build();
    }
}
