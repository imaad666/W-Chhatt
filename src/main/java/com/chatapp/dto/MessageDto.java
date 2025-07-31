package com.chatapp.dto;

import com.chatapp.entity.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class MessageDto {
    
    private Long id;
    
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String content;
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private Message.MessageType messageType;
    private boolean isEdited;
    private LocalDateTime editedAt;
    
    // Constructors
    public MessageDto() {}
    
    public MessageDto(String content, Long roomId) {
        this.content = content;
        this.roomId = roomId;
    }
    
    public MessageDto(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.roomId = message.getRoom().getId();
        this.userId = message.getUser().getId();
        this.username = message.getUser().getUsername();
        this.createdAt = message.getCreatedAt();
        this.messageType = message.getMessageType();
        this.isEdited = message.isEdited();
        this.editedAt = message.getEditedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Message.MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public void setEdited(boolean edited) {
        isEdited = edited;
    }
    
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
} 