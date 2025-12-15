package com.server.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a chat room between a farmer and consultant
 * Created automatically when a consultation is approved
 */
@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The consultation this chat room is associated with
     * One-to-one relationship - each consultation has one chat room
     */
    @OneToOne
    @JoinColumn(name = "consultation_id", unique = true, nullable = false)
    private Consultation consultation;

    /**
     * The farmer participant in this chat
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    /**
     * The consultant participant in this chat
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant consultant;

    /**
     * Whether this chat room is active
     * Can be deactivated when consultation is completed/cancelled
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * All messages in this chat room
     */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * Timestamp when chat room was created
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when chat room was last updated
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Timestamp of the last message sent in this room
     * Used for sorting chat rooms by recent activity
     */
    private LocalDateTime lastMessageAt;

    @Transient
    @Builder.Default
    private Long unreadCount = 0L;
}
