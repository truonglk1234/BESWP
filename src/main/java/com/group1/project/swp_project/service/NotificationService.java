package com.group1.project.swp_project.service;

import com.group1.project.swp_project.entity.Notification;
import com.group1.project.swp_project.repository.NotificationRepository;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    public void notify(Integer userId, String type, String content, String link) {
        Notification noti = new Notification();
        noti.setUser(userRepo.findById(userId).orElseThrow());
        noti.setType(type);
        noti.setContent(content);
        noti.setLink(link);
        noti.setIsRead(false);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepo.save(noti);
    }

    // Lấy danh sách thông báo của user
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepo.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    // Đánh dấu đã đọc
    public void markRead(Integer notiId) {
        Notification n = notificationRepo.findById(notiId).orElseThrow();
        n.setIsRead(true);
        notificationRepo.save(n);
    }


    public void createNotification(Integer userId, String type, String content, String link) {
        Notification noti = new Notification();
        noti.setUser(userRepository.findById(userId).orElse(null));
        noti.setType(type);
        noti.setContent(content);
        noti.setLink(link);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setIsRead(false);
        notificationRepository.save(noti);
    }
}
