package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Role;
import com.group1.project.swp_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :roleName")
    List<User> findAllByRoleName(@Param("roleName") String roleName);
}
