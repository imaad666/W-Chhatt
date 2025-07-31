package com.chatapp.service;

import com.chatapp.dto.ChatRoomDto;
import com.chatapp.entity.ChatRoom;
import com.chatapp.entity.User;
import com.chatapp.repository.ChatRoomRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    public ChatRoomDto createRoom(ChatRoomDto roomDto, String creatorUsername) {
        if (chatRoomRepository.existsByName(roomDto.getName())) {
            throw new RuntimeException("Room name already exists");
        }
        
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChatRoom room = new ChatRoom();
        room.setName(roomDto.getName());
        room.setDescription(roomDto.getDescription());
        room.setCreatedBy(creator);
        room.setPrivate(roomDto.isPrivate());
        room.setMaxParticipants(roomDto.getMaxParticipants());
        room.setCreatedAt(LocalDateTime.now());
        
        // Add creator as participant
        room.getParticipants().add(creator);
        
        ChatRoom savedRoom = chatRoomRepository.save(room);
        
        return convertToDto(savedRoom);
    }
    
    public List<ChatRoomDto> getAllPublicRooms() {
        return chatRoomRepository.findByIsPrivateFalse().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ChatRoomDto> getRoomsByUserId(Long userId) {
        return chatRoomRepository.findRoomsByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<ChatRoomDto> getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .map(this::convertToDto);
    }
    
    public List<ChatRoomDto> searchRooms(String keyword) {
        return chatRoomRepository.findByNameOrDescriptionContaining(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void addUserToRoom(Long roomId, String username) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        room.getParticipants().add(user);
        chatRoomRepository.save(room);
    }
    
    public void removeUserFromRoom(Long roomId, String username) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        room.getParticipants().remove(user);
        chatRoomRepository.save(room);
    }
    
    public List<String> getRoomParticipants(Long roomId) {
        return userRepository.findUsersByRoomId(roomId).stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
    
    public boolean isUserInRoom(Long roomId, String username) {
        return userRepository.findUsersByRoomId(roomId).stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    
    private ChatRoomDto convertToDto(ChatRoom room) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setDescription(room.getDescription());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setPrivate(room.isPrivate());
        dto.setMaxParticipants(room.getMaxParticipants());
        
        if (room.getCreatedBy() != null) {
            dto.setCreatedById(room.getCreatedBy().getId());
            dto.setCreatedByUsername(room.getCreatedBy().getUsername());
        }
        
        Set<String> participants = room.getParticipants().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        dto.setParticipants(participants);
        
        // Get message count
        Long messageCount = messageRepository.countMessagesByRoomId(room.getId());
        dto.setMessageCount(messageCount);
        
        return dto;
    }
} 