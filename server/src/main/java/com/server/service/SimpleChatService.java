package com.server.service;

import com.server.entity.*;
import com.server.enumeration.MessageStatus;
import com.server.repository.ChatMessageRepository;
import com.server.repository.ChatRoomRepository;
import com.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SimpleChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.server.repository.ConsultationRepository consultationRepository;

    @Autowired
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    /**
     * Sync chat rooms for all approved consultations (Recovery tool)
     * Runs automatically on server startup to fix missing rooms.
     */
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public int syncChatRooms() {
        System.out.println("Running Chat Room Recovery Sync...");
        List<Consultation> approved = consultationRepository.findByConsultationRequestStatus(
                com.server.enumeration.ConsultationRequestStatus.APPROVED);
        int count = 0;
        for (Consultation c : approved) {
            Optional<ChatRoom> existing = chatRoomRepository.findByConsultationId(c.getId());
            if (existing.isEmpty()) {
                createChatRoom(c);
                count++;
            }
        }
        System.out.println("Chat Room Recovery Sync Completed. Created " + count + " missing rooms.");
        return count;
    }

    /**
     * Create or retrieve a chat room for a consultation
     */
    @Transactional
    public ChatRoom createChatRoom(Consultation consultation) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByConsultationId(consultation.getId());
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .consultation(consultation)
                .farmer(consultation.getFarmer())
                .consultant(consultation.getConsultant())
                .isActive(true)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    /**
     * Get all chat rooms for a user
     */
    public List<ChatRoom> getUserChatRooms(String email, String role) {
        List<ChatRoom> rooms = List.of();
        if ("FARMER".equalsIgnoreCase(role)) {
            rooms = chatRoomRepository.findByFarmer_Email(email);
        } else if ("CONSULTANT".equalsIgnoreCase(role)) {
            rooms = chatRoomRepository.findByConsultant_Email(email);
        }

        // Populate unread counts
        for (ChatRoom room : rooms) {
            room.setUnreadCount(chatMessageRepository.countUnreadMessagesInRoom(room.getId(), email));
        }

        return rooms;
    }

    /**
     * Send a message (REST only)
     */
    @Transactional
    public ChatMessage sendMessage(Long chatRoomId, String senderEmail, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = sender.getId().equals(chatRoom.getFarmer().getId())
                ? chatRoom.getConsultant()
                : chatRoom.getFarmer();

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Update room timestamp
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        // Broadcast to WebSocket subscribers
        try {
            messagingTemplate.convertAndSend("/topic/room/" + chatRoomId, savedMessage);
        } catch (Exception e) {
            log.error("Failed to broadcast message to WebSocket", e);
        }

        return savedMessage;
    }

    /**
     * Get messages for a room
     */
    public Page<ChatMessage> getChatMessages(Long chatRoomId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageable);
    }

    /**
     * Mark messages as read
     */
    @Transactional
    public void markMessagesAsRead(Long chatRoomId, String userEmail) {
        chatMessageRepository.markAllAsReadInRoom(chatRoomId, userEmail, LocalDateTime.now());
    }

    /**
     * Get unread count
     */
    public Long getUnreadCount(String email) {
        return chatMessageRepository.countUnreadMessages(email);
    }
}
