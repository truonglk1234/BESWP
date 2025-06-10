package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.UpdateProfileRequest;
import com.group1.project.swp_project.dto.UserSummary;
import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository userRepository;
    // KHÔNG cần RoleRepository ở đây

    public List<UserSummary> getUsersByRole(String roleName) {
        // Chỉ cần MỘT lần truy vấn database
        List<User> users = userRepository.findAllByRoleName(roleName);

        return users.stream()
                .map(UserSummary::fromEntity)
                .collect(Collectors.toList());
    }

    public UserSummary updateUser(UserSummary userSummary) {
        Optional<User> optional = userRepository.findById(userSummary.getId());
        if (optional.isPresent()) {
            User user = optional.get();
            user.setEmail(userSummary.getEmail());
            user.setPhone(userSummary.getPhone());
            if (user.getProfile() != null) {
                user.getProfile().setFullName(userSummary.getName());
            }
            User updatedUser = userRepository.save(user);
            return UserSummary.fromEntity(updatedUser);
        }
        throw new RuntimeException("User not found with id: " + userSummary.getId());
    }

    @Transactional
    public UserSummary updateProfile(int Id, UpdateProfileRequest request) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("User not found id: " + Id));
        // check email and phone if have change
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Phone number already exists");
            }
            user.setPhone(request.getPhone());
        }
        // Update profile
        if (user.getProfile() == null) {
            throw new RuntimeException("User not found profile");
        }
        if (request.getFullName() != null) {
            user.getProfile().setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            user.getProfile().setGender(request.getGender());
        }
        if (request.getDateOfBirthday() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBirth = LocalDate.parse(request.getDateOfBirthday(), formatter);
            user.getProfile().setDataOfBirthday(dateOfBirth);
        }
        if (request.getAddress() != null) {
            user.getProfile().setAddress(request.getAddress());
        }
        if (request.getAvatarUrl() != null) {
            user.getProfile().setAvatarUrl(request.getAvatarUrl());
        }
        User updatedUser = userRepository.save(user);
        return UserSummary.fromEntity(updatedUser);
    }

    public UserSummary deleteUser(int Id) {
        Optional<User> user = userRepository.findById(Id);
        if (user.isPresent()) {
            UserSummary deletedUser = UserSummary.fromEntity(user.get());
            userRepository.deleteById(Id);
            return deletedUser;
        }
        throw new RuntimeException("User not found with id: " + Id);
    }
}
