package com.group1.project.swp_project.dto.req;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateUserRequest {
    private String email;
    private String password;
    private String phone;
    private String fullName;
    private Boolean gender;
    private LocalDate dateOfBirthday;
    private String address;
}
