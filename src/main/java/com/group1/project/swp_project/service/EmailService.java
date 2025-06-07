package com.group1.project.swp_project.service;

// --- CÁC IMPORT CẦN THIẾT ---
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
// --- KẾT THÚC IMPORT ---

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // Thư viện để xử lý template HTML

    /**
     * Gửi email xác thực với template HTML.
     *
     * @param to Địa chỉ email người nhận
     * @param subject Tiêu đề email
     * @param verificationCode Mã xác thực để chèn vào template
     */
    public void sendVerificationEmail(String to, String subject, String verificationCode) {
        try {
            // Tạo một context của Thymeleaf để chứa các biến
            Context context = new Context();
            context.setVariable("verification_code", verificationCode); // Đặt biến cho template

            // Xử lý template HTML với các biến đã cung cấp
            // "verification-email" là tên file HTML trong /resources/templates
            String htmlContent = templateEngine.process("verification-email", context);

            // Tạo một MimeMessage để gửi email HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = đây là nội dung HTML

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Xử lý lỗi ở đây, ví dụ: log lại lỗi
            e.printStackTrace();
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }
}