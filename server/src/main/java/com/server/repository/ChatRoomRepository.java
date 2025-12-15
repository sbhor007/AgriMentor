package com.server.repository;

import com.server.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ChatRoom entity
 * Provides database access methods for chat room operations
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * Find chat room by consultation ID
     * 
     * @param consultationId the consultation ID
     * @return Optional containing the chat room if found
     */
    Optional<ChatRoom> findByConsultationId(Long consultationId);

    /**
     * Find all chat rooms where user is either farmer or consultant
     * 
     * @param farmerId     the farmer's user ID
     * @param consultantId the consultant's user ID
     * @return list of chat rooms
     */
    List<ChatRoom> findByFarmerIdOrConsultantId(Long farmerId, Long consultantId);

    /**
     * Find all chat rooms for a user by their email
     * 
     * @param email the user's email
     * @return list of chat rooms
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.farmer.email = :email OR cr.consultant.email = :email ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByUserEmail(@Param("email") String email);

    /**
     * Check if chat room exists for consultation
     * 
     * @param consultationId the consultation ID
     * @return true if chat room exists
     */
    boolean existsByConsultationId(Long consultationId);

    /**
     * Find active chat rooms for a user
     * 
     * @param email the user's email
     * @return list of active chat rooms
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.farmer.email = :email OR cr.consultant.email = :email) AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findActiveRoomsByUserEmail(@Param("email") String email);

    List<ChatRoom> findByFarmer_Email(String email);

    List<ChatRoom> findByConsultant_Email(String email);
}
