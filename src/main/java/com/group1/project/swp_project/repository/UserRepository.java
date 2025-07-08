package com.group1.project.swp_project.repository;

import com.group1.project.swp_project.entity.Role;
import com.group1.project.swp_project.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByPhone(String phone);
    Optional<Users> findById(Long  id);
    boolean existsByPhone(String phone);

    Boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);


    @Query("SELECT u FROM Users u JOIN u.role r WHERE r.roleName = :roleName")
    List<Users> findAllByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM Users u WHERE u.role.roleName = :roleName")
    List<Users> findByRoleName(@Param("roleName") String roleName);


    List<Users> findAllByRole_IdOrderByIdAsc(int roleId);

    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'Staff' ORDER BY u.id ASC")
    List<Users> findAllStaffOrderByIdAsc();
}
