package com.server.controller;

import com.server.entity.ChatMessage;
import com.server.entity.ChatRoom;
import com.server.service.SimpleChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    private SimpleChatService chatService;

    // Helper to get current user details
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // We'll need to fetch role from DB or claims ideally, but for now getting from
    // token logic via Service
    // In SimpleChatService we passed email directly. For role, let's pass it from
    // frontend or infer.

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getUserRooms(@RequestParam String role) {
        return ResponseEntity.ok(chatService.getUserChatRooms(getCurrentUserEmail(), role));
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<Page<ChatMessage>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getChatMessages(roomId, PageRequest.of(page, size)));
    }

    @PostMapping("/room/{roomId}/send")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable Long roomId,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        return ResponseEntity.ok(chatService.sendMessage(roomId, getCurrentUserEmail(), content));
    }

    @PutMapping("/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId) {
        chatService.markMessagesAsRead(roomId, getCurrentUserEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long count = chatService.getUnreadCount(email);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Integer>> syncChatRooms() {
        int created = chatService.syncChatRooms();
        return ResponseEntity.ok(Map.of("created", created));
    }

    @org.springframework.messaging.handler.annotation.MessageMapping("/chat/{roomId}/send")
    public void sendMessageWebSocket(@org.springframework.messaging.handler.annotation.DestinationVariable Long roomId,
            @org.springframework.messaging.handler.annotation.Payload Map<String, String> payload) {
        String content = payload.get("content");
        String email = payload.get("email"); // For demo simplicity (avoids JWT WebSocket interceptor complexity)
        // Service already broadcasts to /topic/room/{roomId}
        chatService.sendMessage(roomId, email, content);
    }
}
