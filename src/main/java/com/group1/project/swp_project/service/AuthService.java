package com.group1.project.swp_project.service;

// Các import cần thiết (SimpleMailMessage đã được xóa)
import com.group1.project.swp_project.dto.*;
import com.group1.project.swp_project.dto.req.LoginRequest;
import com.group1.project.swp_project.dto.req.ResetPasswordWithCodeRequest;
import com.group1.project.swp_project.dto.res.LoginResponse;
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

import java.util.List;

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
    public Users registerUser(RegisterDto registerDto) {
        // ... (logic kiểm tra và tạo user giữ nguyên) ...
        if (registerDto.getPassword() == null || !registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Lỗi: Mật khẩu và xác nhận mật khẩu không khớp!");
        }
        if (userRepository.existsByPhone(registerDto.getPhone())) {
            throw new RuntimeException("Lỗi: Số điện thoại này đã được sử dụng!");
        }
        if (registerDto.getEmail() != null && userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được sử dụng!");
        }

        Role userRole = roleRepository.findByRoleName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException(
                        "Lỗi: Vai trò mặc định '" + DEFAULT_ROLE_NAME + "' không tìm thấy."));
        UserStatus userStatus = userStatusRepository.findByStatusName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new RuntimeException(
                        "Lỗi: Trạng thái người dùng '" + DEFAULT_STATUS_NAME + "' không tìm thấy."));

        Users user = new Users();
        user.setPhone(registerDto.getPhone());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRole(userRole);
        user.setStatus(userStatus);
        user.setEnabled(false);

        Profile profile = new Profile();
        profile.setFullName(registerDto.getName());
        profile.setGender(registerDto.getGender());
        profile.setDateOfBirth(registerDto.getDateOfBirthday());
        profile.setUser(user);
        user.setProfile(profile);

        Users savedUser = userRepository.save(user);

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
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        if (!user.isEnabled()) {
            throw new DisabledException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getRoleName(),
                user.getProfile().getFullName());
        return new LoginResponse(token, user.getRole().getRoleName(), user.getId());
    }

    @Transactional
    public void verifyCode(VerificationRequest request) {
        // ... (giữ nguyên) ...
        Users user = userRepository.findByEmail(request.getEmail())
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

    @Transactional
    public void resendVerificationCode(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nào với email này."));
        if (user.isEnabled()) {
            throw new RuntimeException("Tài khoản này đã được kích hoạt");
        }

        VerificationToken verificationToken = tokenRepository.findByUser(user);
        if (verificationToken == null) {
            String newCodeForNewToken = generateVerificationCode();
            VerificationToken newToken = new VerificationToken(newCodeForNewToken, user);
            tokenRepository.save(newToken);

            emailService.sendVerificationEmail(user.getEmail(), "Mã xác thực tài khoản STI Health", newCodeForNewToken);

            return;
        }

        String newCde = generateVerificationCode();

        verificationToken.setToken(newCde);
        verificationToken.resetExpiryDate();
        tokenRepository.save(verificationToken);

        String subject = "Mã xác thực tài khoản STI Health (Gửi lại)";
        emailService.sendVerificationEmail(user.getEmail(), subject, newCde);
    }

    @Transactional
    public void requestPasswordResetCode(String email) {
        // 1. Tìm user bằng email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));

        // 2. Tạo mã mới
        String code = generateVerificationCode();

        // 3. Tìm xem user đã có token chưa
        VerificationToken passwordResetToken = tokenRepository.findByUser(user);
        if (passwordResetToken == null) {
            // Nếu chưa có, tạo mới
            passwordResetToken = new VerificationToken(code, user);
        } else {
            // Nếu có rồi, cập nhật lại mã và thời gian hết hạn
            passwordResetToken.setToken(code);
            passwordResetToken.resetExpiryDate();
        }
        tokenRepository.save(passwordResetToken);

        // 4. Gửi email chứa mã
        // Bạn nên tạo một template HTML mới cho việc này (password-reset-email.html)
        String subject = "Mã đặt lại mật khẩu cho tài khoản STI Health";
        emailService.sendVerificationEmail(user.getEmail(), subject, code); // Tái sử dụng phương thức gửi email
    }

    @Transactional
    public void resetPasswordWithCode(ResetPasswordWithCodeRequest request) {
        // 1. Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        // 2. Tìm user bằng email
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này."));

        // 3. Tìm token của user đó và xác thực
        VerificationToken token = tokenRepository.findByUser(user);
        if (token == null || !token.getToken().equals(request.getCode())
                || token.getExpiryDate().before(new java.util.Date())) {
            throw new RuntimeException("Mã xác thực không hợp lệ hoặc đã hết hạn.");
        }

        // 4. Mã hóa mật khẩu mới và cập nhật cho user
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 5. Xóa token sau khi đã sử dụng thành công
        tokenRepository.delete(token);
    }

    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ");
        }
        jwtUtil.invalidateToken(token);
    }
}