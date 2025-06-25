package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.Notification;
import com.group1.project.swp_project.repository.UserRepository;
import com.group1.project.swp_project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;
    @Autowired
    UserRepository userRepo;

    // Lấy thông báo user hiện tại (token)
    @GetMapping("/me")
    public List<Notification> getMyNotis(Authentication auth) {
        String email = auth.getName();
        Integer userId = userRepo.findByEmail(email).orElseThrow().getId();
        return notificationService.getUserNotifications(userId);
    }

    // Đánh dấu đã đọc
    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Integer id) {
        notificationService.markRead(id);
    }
}

