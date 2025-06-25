package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer>
{
    List<Notification> findByIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
}
