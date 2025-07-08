package com.group1.project.swp_project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(ProfileRepository profileRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users getCurrentUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        Optional<Users> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) return false;

        Users user = optional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}