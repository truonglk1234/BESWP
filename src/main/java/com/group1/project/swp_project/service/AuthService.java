package com.group1.project.swp_project.service;

// Các import cần thiết (SimpleMailMessage đã được xóa)
import com.group1.project.swp_project.dto.LoginRequest;
import com.group1.project.swp_project.dto.LoginResponse;
import com.group1.project.swp_project.dto.RegisterDto;
import com.group1.project.swp_project.dto.VerificationRequest;
import com.group1.project.swp_project.entity.*;
import com.group1.project.swp_project.repository.RoleRepository;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.repository.UserStatusRepository;
import com.group1.project.swp_project.repository.VerificationTokenRepository;
import com.group1.project.swp_project.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService; // EmailService đã được nâng cấp

    // ... (constructor và các phương thức khác giữ nguyên) ...
    public static final String DEFAULT_ROLE_NAME = "Customer";
    public static final String DEFAULT_STATUS_NAME = "Inactive";

    @Autowired
    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserStatusRepository userStatusRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       VerificationTokenRepository tokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userStatusRepository = userStatusRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    private String generateVerificationCode() {
        return String.format("%06d", new java.util.Random().nextInt(999999));
    }


    @Transactional
    public User registerUser(RegisterDto registerDto) {
        // ... (logic kiểm tra và tạo user giữ nguyên) ...
        if (registerDto.getPassword() == null || !registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Lỗi: Mật khẩu và xác nhận mật khẩu không khớp!");
        }
        if (userRepository.existsByUserPhone(registerDto.getUserPhone())) {
            throw new RuntimeException("Lỗi: Số điện thoại này đã được sử dụng!");
        }
        if (registerDto.getEmail() != null && userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được sử dụng!");
        }

        Role userRole = roleRepository.findByRoleName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("Lỗi: Vai trò mặc định '" + DEFAULT_ROLE_NAME + "' không tìm thấy."));
        UserStatus userStatus = userStatusRepository.findByStatusName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new RuntimeException("Lỗi: Trạng thái người dùng '" + DEFAULT_STATUS_NAME + "' không tìm thấy."));

        User user = new User();
        user.setUserPhone(registerDto.getUserPhone());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRole(userRole);
        user.setStatus(userStatus);
        user.setEnabled(false);

        Profile profile = new Profile();
        profile.setFullName(registerDto.getFullName());
        profile.setGender(registerDto.getGender());
        profile.setDataOfBirthday(registerDto.getDateOfBirthday());
        profile.setUser(user);
        user.setProfile(profile);

        User savedUser = userRepository.save(user);

        String code = generateVerificationCode();
        VerificationToken verificationToken = new VerificationToken(code, savedUser);
        tokenRepository.save(verificationToken);

        // --- ĐÂY LÀ CHỖ THAY ĐỔI ---
        String recipientAddress = savedUser.getEmail();
        String subject = "Mã xác thực tài khoản STI Health"; // Tên ứng dụng của bạn

        // Gọi phương thức mới của EmailService để gửi email HTML
        emailService.sendVerificationEmail(recipientAddress, subject, code);
        // --- KẾT THÚC THAY ĐỔI ---

        return savedUser;
    }

    public LoginResponse login(LoginRequest request) {
        // ... (giữ nguyên) ...
        User user = userRepository.findByUserPhone(request.getUserPhone())
                .orElseThrow(() -> new RuntimeException("Số điện thoại không tồn tại"));
        if (!user.isEnabled()) {
            throw new DisabledException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }
        String token = jwtUtil.generateToken(user.getUserPhone(), user.getRole().getRoleName());
        return new LoginResponse(token, user.getRole().getRoleName(), user.getId());
    }

    @Transactional
    public void verifyCode(VerificationRequest request) {
        // ... (giữ nguyên) ...
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này."));

        VerificationToken verificationToken = tokenRepository.findByUser(user);
        if (verificationToken == null) {
            throw new RuntimeException("Yêu cầu xác thực không hợp lệ hoặc tài khoản đã được kích hoạt.");
        }
        if (verificationToken.getExpiryDate().before(new java.util.Date())) {
            tokenRepository.delete(verificationToken);
            userRepository.delete(user);
            throw new RuntimeException("Mã xác thực đã hết hạn. Vui lòng đăng ký lại.");
        }
        if (!verificationToken.getToken().equals(request.getCode())) {
            throw new RuntimeException("Mã xác thực không chính xác.");
        }

        user.setEnabled(true);
        UserStatus activeStatus = userStatusRepository.findByStatusName("Active")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy trạng thái 'Active'."));
        user.setStatus(activeStatus);

        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}