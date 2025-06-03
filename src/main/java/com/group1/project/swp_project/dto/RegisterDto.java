package com.group1.project.swp_project.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterDto {

    @NotBlank
    @Size(max = 100, message = "Họ tên không được đề trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Emai không được vượt quá 100 ký tự")
    private String email;

    private Boolean gender;

    private LocalDate dateOfBirthday;


    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 9, max = 12, message = "Số điện thoại phải từ 9 đến 12 ký tự")
    // Ví dụ regex cho số điện thoại Việt Nam (có thể cần điều chỉnh)
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "Số điện thoại không đúng định dạng Việt Nam")
    private String userPhone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6 đến 20 ký tự")

    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @AssertTrue(message = "Bạn phải đồng ý với Điều khaon3 sử dụng và Chính sách bảo mật")
    private boolean agreedToTerms;

}
