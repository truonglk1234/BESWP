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

}