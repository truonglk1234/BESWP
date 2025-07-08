package com.group1.project.swp_project.dto.req;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
