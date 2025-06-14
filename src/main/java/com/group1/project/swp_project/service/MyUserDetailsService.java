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
        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new UsernameNotFoundException("..."));

        // Lấy tên vai trò từ DB (ví dụ: "Admin", "Manager"...)
        String roleName = user.getRole().getRoleName();

        // Tạo một GrantedAuthority từ tên vai trò đó
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // Trả về UserDetails với quyền này
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                Collections.singletonList(authority) // Đảm bảo quyền được đưa vào đây
        );
    }
}