package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserPhone(String userPhone);
    boolean existsByUserPhone(String userPhone);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
