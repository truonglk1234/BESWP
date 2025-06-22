package com.group1.project.swp_project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.UserProfileDto;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.ProfileRepository;
import com.group1.project.swp_project.repository.UserRepository;

@Service
public class UserService {
    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    public UserService(ProfileRepository profileRepository, UserRepository userRepository) {
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
                profile.getAvatarUrl());
    }

    public Users getUserByEmail(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public void updateUserProfile(Users user, UserProfileDto dto) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        profile.setFullName(dto.getFullName());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setAddress(dto.getAddress());
        // profile.setAvatarUrl(dto.getAvatarUrl()); // nếu cho đổi avatar

        user.setPhone(dto.getPhone());

        profileRepository.save(profile);
        userRepository.save(user);
    }
}
