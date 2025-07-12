package com.group1.project.swp_project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.group1.project.swp_project.dto.UserMonthlyStatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group1.project.swp_project.dto.ConsultantDTO;
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

    public UserService(ProfileRepository profileRepository, UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<ConsultantDTO> getAllConsultants(String specialty, String gender) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllConsultants'");
    }

    public List<UserMonthlyStatDTO> getMonthlyUserStats() {
        List<Object[]> rawData = userRepository.getMonthlyUserStats();
        List<UserMonthlyStatDTO> result = new ArrayList<>();
        for (Object[] row : rawData) {
            result.add(new UserMonthlyStatDTO((String) row[0], ((Number) row[1]).longValue()));
        }
        return result;
    }
}