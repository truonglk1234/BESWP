package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.RegisterDto;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Role;
import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.entity.UserStatus;
// SỬA TÊN REPOSITORY Ở ĐÂY
import com.group1.project.swp_project.repository.RoleRepository; // Đảm bảo tên file và class là RoleRepository
import com.group1.project.swp_project.repository.UserRepository;
// SỬA TÊN REPOSITORY Ở ĐÂY
import com.group1.project.swp_project.repository.UserStatusRepository; // Đảm bảo tên file và class là UserStatusRepository

// SỬA IMPORT TRANSACTIONAL
import org.springframework.transaction.annotation.Transactional; // Sử dụng của Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Các import khác nếu có (ví dụ: java.time.LocalDateTime cho User entity nếu bạn set createdAt ở đây)

@Service
public class AuthService {

    private final UserRepository userRepository;
    // SỬA TÊN BIẾN VÀ KIỂU
    private final RoleRepository roleRepository;
    // SỬA TÊN BIẾN VÀ KIỂU
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;

    public static final String DEFAULT_ROLE_NAME = "Customer";
    public static final String DEFAULT_STATUS_NAME = "Active";

    @Autowired
    public AuthService(UserRepository userRepository,
                       // SỬA THAM SỐ CONSTRUCTOR
                       RoleRepository roleRepository,
                       // SỬA THAM SỐ CONSTRUCTOR
                       UserStatusRepository userStatusRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userStatusRepository = userStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Sử dụng org.springframework.transaction.annotation.Transactional
    public User registerUser(RegisterDto registerDto) {
        // 1. Kiểm tra password và confirmPassword
        if (registerDto.getPassword() == null || registerDto.getConfirmPassword() == null ||
                !registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Lỗi: Mật khẩu và xác nhận mật khẩu không khớp hoặc bị bỏ trống!");
        }

        // 2. Kiểm tra userPhone đã tồn tại chưa
        if (userRepository.existsByUserPhone(registerDto.getUserPhone())) {
            throw new RuntimeException("Lỗi: Số điện thoại này đã được sử dụng!");
        }


        if (registerDto.getEmail() != null && !registerDto.getEmail().isEmpty() &&
                userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được sử dụng!");
        }


        Role userRole = roleRepository.findByRoleName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("Lỗi: Vai trò mặc định '" + DEFAULT_ROLE_NAME + "' không tìm thấy. Vui lòng kiểm tra data.sql hoặc dữ liệu bảng Role."));


        UserStatus userStatus = userStatusRepository.findByStatusName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new RuntimeException("Lỗi: Trạng thái người dùng mặc định '" + DEFAULT_STATUS_NAME + "' không tìm thấy. Vui lòng kiểm tra data.sql hoặc dữ liệu bảng UserStatus."));


        User user = new User();
        user.setUserPhone(registerDto.getUserPhone());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRole(userRole);

        user.setStatus(userStatus);


        Profile profile = new Profile();
        profile.setFullName(registerDto.getFullName());
        profile.setGender(registerDto.getGender());
        if (registerDto.getDateOfBirthday() != null) {
            profile.setDataOfBirthday(registerDto.getDateOfBirthday()); // Đảm bảo tên phương thức setter đúng
        }

        profile.setUser(user);
        user.setProfile(profile); // Liên kết hai chiều

        return userRepository.save(user);
    }
}