package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ChangePasswordRequestDto;
import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.dto.req.UpdateProfileDto;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.ProfileRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(ProfileRepository profileRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileDto getUserProfile(Users user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ người dùng"));

        return new UserProfileDto(
                profile.getFullName(),
                profile.getGender(),
                profile.getDateOfBirth(),
                user.getEmail(),
                user.getPhone(),
                profile.getAddress(),
                profile.getAvatarUrl(),
                user.getRole().getRoleName(),
                user.getCreatedAt(),
                user.getEnabled(),
                profile.getSpecialty(),
                profile.getExperienceYears(),
                profile.getConsultationFee(),
                profile.getDescription(),
                profile.getEducation(),
                profile.getCertifications());
    }

    public void updateUserProfile(Users user, UpdateProfileDto dto) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        profile.setFullName(dto.getFullName());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirth()); // ✅ đồng bộ tên trường
        profile.setAddress(dto.getAddress());
        profile.setSpecialty(dto.getSpecialty());
        profile.setExperienceYears(dto.getExperienceYears());
        profile.setConsultationFee(dto.getConsultationFee());
        profile.setEducation(dto.getEducation());
        profile.setCertifications(dto.getCertifications());

        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());

        MultipartFile avatar = dto.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatar";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                avatar.transferTo(filePath.toFile());

                profile.setAvatarUrl("/avatars/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi lưu avatar", e);
            }
        }

        profileRepository.save(profile);
        userRepository.save(user);
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    public void changePassword(String email, ChangePasswordRequestDto request) {
        Users user = getUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public Users getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
