package com.group1.project.swp_project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.lang.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy header Authorization từ request
        String authHeader = request.getHeader("Authorization");

        // 2. Kiểm tra xem có phải Bearer Token không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Bỏ "Bearer " phía trước

            try {
                // 3. Trích xuất thông tin từ token
                String userPhone = jwtUtil.extractUserPhone(token);
                String role = jwtUtil.extractRole(token);

                // 4. Tạo đối tượng xác thực và gán vào SecurityContext
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPhone,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)) // thêm ROLE_ prefix
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // 5. Nếu token không hợp lệ, trả về lỗi 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // 6. Cho phép đi tiếp đến filter tiếp theo
        filterChain.doFilter(request, response);
    }

    /**
     * (Tuỳ chọn) Nếu bạn muốn bỏ qua kiểm tra JWT cho một số URL (như login,
     * register)
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        List<String> excludedPaths = List.of("/auth/login", "/auth/register");

        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}
