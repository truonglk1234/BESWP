package com.group1.project.swp_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group1.project.swp_project.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

}
