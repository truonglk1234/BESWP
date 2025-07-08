package com.group1.project.swp_project.dto.req;

import lombok.Data;

@Data
public class SetNewPasswordRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
