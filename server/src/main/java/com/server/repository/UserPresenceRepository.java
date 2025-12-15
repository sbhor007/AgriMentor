package com.server.repository;

import com.server.entity.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserPresence entity
 * Provides database access methods for user presence/online status
 */
@Repository
public interface UserPresenceRepository extends JpaRepository<UserPresence, Long> {

    /**
     * Find presence record by user ID
     * 
     * @param userId the user's ID
     * @return Optional containing the presence record if found
     */
    Optional<UserPresence> findByUserId(Long userId);

    /**
     * Find presence record by user email
     * 
     * @param email the user's email
     * @return Optional containing the presence record if found
     */
    @Query("SELECT up FROM UserPresence up WHERE up.user.email = :email")
    Optional<UserPresence> findByUserEmail(@Param("email") String email);

    /**
     * Check if user is currently online
     * 
     * @param email the user's email
     * @return true if user is online
     */
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPresence up WHERE up.user.email = :email AND up.isOnline = true")
    boolean isUserOnline(@Param("email") String email);
}
