package com.server.repository;

import com.server.entity.ChatMessage;
import com.server.enumeration.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository for ChatMessage entity
 * Provides database access methods for message operations
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find messages in a chat room with pagination, ordered by sent time
     * 
     * @param roomId   the chat room ID
     * @param pageable pagination information
     * @return page of messages
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findByChatRoomIdOrderBySentAtDesc(@Param("roomId") Long roomId, Pageable pageable);

    /**
     * Count unread messages for a user across all chat rooms
     * 
     * @param email the user's email
     * @return count of unread messages
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.receiver.email = :email AND cm.status != 'READ' AND cm.isDeleted = false")
    Long countUnreadMessages(@Param("email") String email);

    /**
     * Count unread messages in a specific chat room for a user
     * 
     * @param roomId the chat room ID
     * @param email  the user's email
     * @return count of unread messages
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.receiver.email = :email AND cm.status != 'READ' AND cm.isDeleted = false")
    Long countUnreadMessagesInRoom(@Param("roomId") Long roomId, @Param("email") String email);

    /**
     * Mark all unread messages in a room as read
     * 
     * @param roomId the chat room ID
     * @param email  the receiver's email
     * @param readAt the timestamp to set as read time
     */
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.status = 'READ', cm.readAt = :readAt WHERE cm.chatRoom.id = :roomId AND cm.receiver.email = :email AND cm.status != 'READ'")
    void markAllAsReadInRoom(@Param("roomId") Long roomId, @Param("email") String email,
            @Param("readAt") LocalDateTime readAt);

    /**
     * Get the most recent message in a chat room
     * 
     * @param roomId the chat room ID
     * @return the most recent message
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false ORDER BY cm.sentAt DESC LIMIT 1")
    ChatMessage findMostRecentMessageInRoom(@Param("roomId") Long roomId);

    /**
     * Find all messages with a specific status for a user
     * 
     * @param receiverEmail the receiver's email
     * @param status        the message status
     * @return list of messages
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.receiver.email = :receiverEmail AND cm.status = :status AND cm.isDeleted = false ORDER BY cm.sentAt DESC")
    Page<ChatMessage> findByReceiverAndStatus(@Param("receiverEmail") String receiverEmail,
            @Param("status") MessageStatus status, Pageable pageable);
}
