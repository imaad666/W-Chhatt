package com.chatapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public class ChatRoomDto {
    
    private Long id;
    
    @NotBlank(message = "Room name is required")
    @Size(min = 3, max = 100, message = "Room name must be between 3 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private LocalDateTime createdAt;
    private boolean isPrivate;
    private Integer maxParticipants;
    private Long createdById;
    private String createdByUsername;
    private Set<String> participants;
    private Long messageCount;
    
    // Constructors
    public ChatRoomDto() {}
    
    public ChatRoomDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }
    
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
    
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public Long getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
    
    public Set<String> getParticipants() {
        return participants;
    }
    
    public void setParticipants(Set<String> participants) {
        this.participants = participants;
    }
    
    public Long getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }
} 