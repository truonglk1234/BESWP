package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.dto.req.MenstrualCycleRequest;
import com.group1.project.swp_project.entity.MenstrualCycle;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.service.MenstrualCycleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Tag(name = "Khai báo và thông báo cho chu kì kinh nguyệt", description = "APIs for Customer")
@RestController
@RequestMapping("/api/auth/health")
public class MenstrualCycleController {
    @Autowired
    private MenstrualCycleService service;
    @Autowired
    private UserRepository userRepo;

    // Lấy userId từ Authentication
    private Integer getCurrentUserId(Authentication auth) {
        String email = auth.getName();
        return userRepo.findByEmail(email).orElseThrow().getId();
    }

    // Lấy chu kỳ gần nhất
    @GetMapping("/me")
    public MenstrualCycle getMyCycle(Authentication authentication) {
        return service.getCurrent(getCurrentUserId(authentication));
    }

    // Lấy lịch sử 3 chu kỳ gần nhất
    @GetMapping("/history")
    public List<MenstrualCycle> getMyHistory(Authentication authentication) {
        return service.getHistory(getCurrentUserId(authentication))
                .stream().limit(3).collect(Collectors.toList());
    }

    // Lưu hoặc cập nhật chu kỳ
    @PostMapping("/me")
    public MenstrualCycle createOrUpdateCycle(@RequestBody MenstrualCycleRequest req, Authentication authentication) {
        return service.saveOrUpdate(req, getCurrentUserId(authentication));
    }
}
