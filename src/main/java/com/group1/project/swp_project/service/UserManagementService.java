package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.UserSummary;
import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
