package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.UserSummary;
import com.group1.project.swp_project.dto.req.CreateUserRequest;
import com.group1.project.swp_project.dto.req.UpdateProfileRequest;
import com.group1.project.swp_project.entity.Profile;
import com.group1.project.swp_project.entity.Role;
import com.group1.project.swp_project.entity.UserStatus;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.RoleRepository;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository userRepository;
    // KHÔNG cần RoleRepository ở đây
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;
    public List<UserSummary> getUsersByRole(String roleName) {
        // Chỉ cần MỘT lần truy vấn database
        List<Users> users = userRepository.findAllByRoleName(roleName);

        return users.stream()
                .map(UserSummary::fromEntity)
                .collect(Collectors.toList());
    }

    public UserSummary updateUser(UserSummary userSummary) {
        Optional<Users> optional = userRepository.findById(userSummary.getId());
        if (optional.isPresent()) {
            Users user = optional.get();
            user.setEmail(userSummary.getEmail());
            user.setPhone(userSummary.getPhone());
            if (user.getProfile() != null) {
                user.getProfile().setFullName(userSummary.getName());
            }
            Users updatedUser = userRepository.save(user);
            return UserSummary.fromEntity(updatedUser);
        }
        throw new RuntimeException("User not found with id: " + userSummary.getId());
    }

    @Transactional
    public UserSummary updateProfile(int Id, UpdateProfileRequest request) {
        Users user = userRepository.findById(Id)
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
            user.getProfile().setDateOfBirth(request.getDateOfBirthday());
        }
        if (request.getAddress() != null) {
            user.getProfile().setAddress(request.getAddress());
        }
        if (request.getAvatarUrl() != null) {
            user.getProfile().setAvatarUrl(request.getAvatarUrl());
        }
        Users updatedUser = userRepository.save(user);
        return UserSummary.fromEntity(updatedUser);
    }

    public UserSummary deleteUser(int Id) {
        Optional<Users> user = userRepository.findById(Id);
        if (user.isPresent()) {
            UserSummary deletedUser = UserSummary.fromEntity(user.get());
            userRepository.deleteById(Id);
            return deletedUser;
        }
        throw new RuntimeException("User not found with id: " + Id);
    }


    @Transactional
    public UserSummary createStaff(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        Role role = roleRepository.findById(4)
                .orElseThrow(() -> new RuntimeException("Role ID 4 (Staff) not found"));
        user.setRole(role);

        UserStatus status = userStatusRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Status ID 1 (Active) not found"));
        user.setStatus(status);

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        profile.setGender(request.getGender());
        profile.setDateOfBirth(request.getDateOfBirthday());
        profile.setAddress(request.getAddress());
        profile.setUser(user);

        user.setProfile(profile);

        Users saved = userRepository.save(user);
        return UserSummary.fromEntity(saved);
    }
}
