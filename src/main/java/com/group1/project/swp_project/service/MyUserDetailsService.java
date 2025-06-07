package com.group1.project.swp_project.service; // Đảm bảo package đúng

import com.group1.project.swp_project.entity.User;
import com.group1.project.swp_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException; // Import thêm cái này
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service // QUAN TRỌNG: Đánh dấu đây là một Spring Service Bean
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // SỬA: Dùng constructor injection thay vì field injection
    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userPhone) throws UsernameNotFoundException {
        // SỬA: Tìm user bằng userPhone, vì đây là thông tin bạn lưu trong JWT token
        User user = userRepository.findByUserPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với số điện thoại: " + userPhone));

        // KIỂM TRA: Đảm bảo tài khoản đã được kích hoạt
        if (!user.isEnabled()) {
            throw new DisabledException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn.");
        }

        // SỬA: Tạo danh sách quyền (authorities) từ Role của user
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName()));

        // Trả về một đối tượng UserDetails mà Spring Security có thể hiểu
        return new org.springframework.security.core.userdetails.User(
                user.getUserPhone(), // Username (định danh chính)
                user.getPassword(),  // Mật khẩu đã mã hóa
                authorities          // Danh sách các quyền
        );
    }
}