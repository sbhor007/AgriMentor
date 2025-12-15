package com.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity tracking user online/offline presence status
 * Used to show real-time availability in chat interface
 */
@Entity
@Table(name = "user_presence", indexes = {
        @Index(name = "idx_user_online", columnList = "user_id, is_online")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserPresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user this presence record belongs to
     * One-to-one relationship - each user has one presence record
     */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
     * Whether the user is currently online (connected to WebSocket)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isOnline = false;

    /**
     * Last time the user was seen online
     * Updated when user disconnects or sends a message
     */
    @Column(nullable = false)
    private LocalDateTime lastSeenAt;

    /**
     * Timestamp when this record was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
