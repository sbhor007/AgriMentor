package com.server.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.server.enumeration.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.dto.RegisterRequest;
import com.server.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    void save(RegisterRequest request);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String username);

    Boolean existsByEmailAndRole(String email, Role role);

    // Count users by role
    Long countByRole(Role role);

    // Count active/inactive users
    Long countByIsActive(Boolean isActive);

    // Count verified/unverified users
    Long countByIsVerified(Boolean isVerified);

    // Get all users by role
    List<User> findByRole(Role role);

    // Get recently registered users
    List<User> findTop10ByOrderByCreatedAtDesc();

    // Count users created after a specific date
    Long countByRoleAndCreatedAtAfter(Role role, LocalDateTime date);
}
