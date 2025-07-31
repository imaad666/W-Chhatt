package com.chatapp.controller;

import com.chatapp.dto.MessageDto;
import com.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDto>> getMessagesByRoomId(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<MessageDto> messages = messageService.getMessagesByRoomId(roomId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/room/{roomId}/search")
    public ResponseEntity<List<MessageDto>> searchMessagesInRoom(
            @PathVariable Long roomId,
            @RequestParam String keyword) {
        
        List<MessageDto> messages = messageService.searchMessagesInRoom(keyword, roomId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDto>> getUserMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getUserMessages(userId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/user/{userId}/room/{roomId}")
    public ResponseEntity<List<MessageDto>> getUserMessagesInRoom(
            @PathVariable Long userId,
            @PathVariable Long roomId) {
        
        List<MessageDto> messages = messageService.getUserMessagesInRoom(userId, roomId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long messageId) {
        return messageService.getMessageById(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request) {
        
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String newContent = request.get("content");
            
            if (newContent == null || newContent.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Message content cannot be empty");
                return ResponseEntity.badRequest().body(error);
            }
            
            MessageDto updatedMessage = messageService.updateMessage(messageId, newContent, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Message updated successfully");
            response.put("message", updatedMessage);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            messageService.deleteMessage(messageId, username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/room/{roomId}/count")
    public ResponseEntity<Long> getMessageCountByRoomId(@PathVariable Long roomId) {
        Long count = messageService.getMessageCountByRoomId(roomId);
        return ResponseEntity.ok(count);
    }
} 