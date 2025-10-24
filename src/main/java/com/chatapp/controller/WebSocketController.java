package com.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chatapp.dto.MessageDto;
import com.chatapp.service.MessageService;

@Controller
public class WebSocketController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        String username = "Anonymous";
        var user = headerAccessor.getUser();
        if (user != null) {
            var userName = user.getName();
            if (userName != null) {
                username = userName;
            }
        }
        MessageDto savedMessage = messageService.saveMessage(messageDto, username);
        
        // Send to specific room
        messagingTemplate.convertAndSend("/topic/room." + messageDto.getRoomId(), savedMessage);
        
        return savedMessage;
    }
    
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public MessageDto addUser(@Payload MessageDto messageDto, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        var sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put("username", messageDto.getUsername());
            sessionAttributes.put("roomId", messageDto.getRoomId());
        }
        
        // Send join message to room
        MessageDto joinMessage = new MessageDto();
        joinMessage.setContent(messageDto.getUsername() + " joined the chat!");
        joinMessage.setUsername("System");
        joinMessage.setMessageType(com.chatapp.entity.Message.MessageType.SYSTEM);
        
        messagingTemplate.convertAndSend("/topic/room." + messageDto.getRoomId(), joinMessage);
        
        return joinMessage;
    }
    
    @MessageMapping("/chat.leaveUser")
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor) {
        String username = null;
        Long roomId = null;
        
        var sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            username = (String) sessionAttributes.get("username");
            roomId = (Long) sessionAttributes.get("roomId");
        }
        
        if (username != null && roomId != null) {
            MessageDto leaveMessage = new MessageDto();
            leaveMessage.setContent(username + " left the chat!");
            leaveMessage.setUsername("System");
            leaveMessage.setMessageType(com.chatapp.entity.Message.MessageType.SYSTEM);
            
            messagingTemplate.convertAndSend("/topic/room." + roomId, leaveMessage);
        }
    }
} 