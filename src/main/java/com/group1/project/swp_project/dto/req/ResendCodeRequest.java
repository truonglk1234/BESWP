package com.group1.project.swp_project.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendCodeRequest {
    @NotBlank(message = "Email kkhông được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

}
