package com.chatapp.controller;

import com.chatapp.dto.ChatRoomDto;
import com.chatapp.service.ChatRoomService;
import com.chatapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class ChatRoomController {
    
    @Autowired
    private ChatRoomService chatRoomService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody ChatRoomDto roomDto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            ChatRoomDto createdRoom = chatRoomService.createRoom(roomDto, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Room created successfully");
            response.put("room", createdRoom);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> getAllPublicRooms() {
        List<ChatRoomDto> rooms = chatRoomService.getAllPublicRooms();
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<ChatRoomDto>> getMyRooms() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userService.findByUsername(username)
                    .map(user -> {
                        List<ChatRoomDto> rooms = chatRoomService.getRoomsByUserId(user.getId());
                        return ResponseEntity.ok(rooms);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDto> getRoomById(@PathVariable Long roomId) {
        return chatRoomService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ChatRoomDto>> searchRooms(@RequestParam String keyword) {
        List<ChatRoomDto> rooms = chatRoomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }
    
    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            chatRoomService.addUserToRoom(roomId, username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully joined the room");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable Long roomId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            chatRoomService.removeUserFromRoom(roomId, username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully left the room");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<String>> getRoomParticipants(@PathVariable Long roomId) {
        List<String> participants = chatRoomService.getRoomParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
} 