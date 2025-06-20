package com.group1.project.swp_project.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private int userId;
}