package com.group1.project.swp_project.dto.req;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private Boolean gender;
    private LocalDate dateOfBirthday;
    private String address;
    private String avatarUrl;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phone;
}