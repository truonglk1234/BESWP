package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ConsultantDTO;
import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.repository.ExaminationFeedbackRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultantService {

    private final UserRepository userRepository;
    private final ExaminationFeedbackRepository feedbackRepository;

    public ConsultantService(UserRepository userRepository, ExaminationFeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public List<ConsultantDTO> getAllConsultants(String specialty, String gender) {
        List<Users> consultants = userRepository.findAllByRoleName("CONSULTANT");

        return consultants.stream()
                .filter(u -> u.getProfile() != null)
                .filter(u -> specialty == null || "Tất cả".equalsIgnoreCase(specialty)
                        || specialty.equalsIgnoreCase(u.getProfile().getSpecialty()))
                .filter(u -> {
                    if (gender == null || "Tất cả".equalsIgnoreCase(gender)) {
                        return true;
                    }
                    return gender.equalsIgnoreCase("Nam")
                            ? Boolean.TRUE.equals(u.getProfile().getGender())
                            : Boolean.FALSE.equals(u.getProfile().getGender());
                })
                .map(u -> {
                    Double avgRating = feedbackRepository.getAverageRatingByConsultantId(u.getId());
                    return ConsultantDTO.fromUser(u, avgRating);
                })
                .collect(Collectors.toList());
    }

}
