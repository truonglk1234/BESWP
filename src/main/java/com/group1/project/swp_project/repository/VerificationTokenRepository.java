package com.group1.project.swp_project.repository; // Quan trọng: Phải đúng package

import com.group1.project.swp_project.entity.Users;
import com.group1.project.swp_project.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Rất quan trọng! Annotation này để Spring biết đây là một Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // Tìm token dựa trên chuỗi token
    VerificationToken findByToken(String token);

    // Tìm token dựa trên User (hữu ích nếu bạn muốn gửi lại mail)
    VerificationToken findByUser(Users user);
}