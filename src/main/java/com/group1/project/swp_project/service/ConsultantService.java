package com.group1.project.swp_project.service;

import com.group1.project.swp_project.dto.ConsultantDTO;
import com.group1.project.swp_project.entity.Consultant;
import com.group1.project.swp_project.repository.ConsultantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultantService {

    private final ConsultantRepository consultantRepository;

    public ConsultantService(ConsultantRepository consultantRepository) {
        this.consultantRepository = consultantRepository;
    }

    public List<ConsultantDTO> getAllConsultants(String specialty, String gender) {
        List<Consultant> consultants = consultantRepository.findAll();

        return consultants.stream()
                .filter(c -> specialty == null || "Tất cả".equalsIgnoreCase(specialty)
                        || c.getSpecialty().equalsIgnoreCase(specialty))
                .filter(c -> {
                    if (gender == null || "Tất cả".equalsIgnoreCase(gender))
                        return true;
                    return gender.equalsIgnoreCase("Nam") ? Boolean.TRUE.equals(c.getGender())
                            : Boolean.FALSE.equals(c.getGender());
                })
                .map(c -> ConsultantDTO.builder()
                        .fullName(c.getFullName())
                        .specialty(c.getSpecialty())
                        .avatarUrl(c.getAvatarUrl())
                        .gender(c.getGender())
                        .experienceYears(c.getExperienceYears())
                        .description(c.getDescription())
                        .email(c.getEmail())
                        .phone(c.getPhone())
                        .build())
                .collect(Collectors.toList());
    }
}
