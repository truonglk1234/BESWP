package com.group1.project.swp_project.config; // Hoặc package của bạn

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // THÊM ANNOTATION NÀY
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// BỎ import org.springframework.web.servlet.config.annotation.EnableWebMvc; NẾU KHÔNG CẦN THIẾT CHO CẤU HÌNH MVC RIÊNG
// @EnableWebSecurity đã bao gồm nhiều cấu hình cần thiết cho security web.

@Configuration
@EnableWebSecurity // Sử dụng @EnableWebSecurity thay vì @EnableWebMvc cho cấu hình Security
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
            // Các đường dẫn khác nếu cần
    };

    // Mã hóa mật khẩu người dùng
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()    // Cho phép API đăng ký/đăng nhập
                        .requestMatchers(SWAGGER_WHITELIST).permitAll() // <<<--- THÊM DÒNG NÀY ĐỂ CHO PHÉP SWAGGER
                        .anyRequest().authenticated()                   // Các request khác vẫn cần xác thực
                );
        return http.build();
    }
}