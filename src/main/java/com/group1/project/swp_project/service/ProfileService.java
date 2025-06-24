package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.dto.req.UpdateProfileDto;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.ProfileRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
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
                user.getEnabled());
    }

    public void updateUserProfile(Users user, UpdateProfileDto dto) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        profile.setFullName(dto.getFullName());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirthday());
        profile.setAddress(dto.getAddress());
        user.setPhone(dto.getPhone());

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
        Optional<Users> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
}
