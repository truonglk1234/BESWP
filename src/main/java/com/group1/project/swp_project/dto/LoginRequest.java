package com.group1.project.swp_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    private String email;      // có thể null nếu dùng phone
    @NotBlank
    private String password;
}