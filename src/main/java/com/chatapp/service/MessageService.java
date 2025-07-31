package com.chatapp.service;

import com.chatapp.dto.MessageDto;
import com.chatapp.entity.ChatRoom;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.repository.ChatRoomRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    public MessageDto saveMessage(MessageDto messageDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChatRoom room = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        Message message = new Message();
        message.setContent(messageDto.getContent());
        message.setUser(user);
        message.setRoom(room);
        message.setMessageType(Message.MessageType.TEXT);
        message.setCreatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        return new MessageDto(savedMessage);
    }
    
    public List<MessageDto> getMessagesByRoomId(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        
        return messagePage.getContent().stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getNewMessagesByRoomId(Long roomId, LocalDateTime since) {
        return messageRepository.findNewMessagesByRoomId(roomId, since).stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getUserMessages(Long userId) {
        return messageRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getUserMessagesInRoom(Long userId, Long roomId) {
        return messageRepository.findUserMessagesInRoom(userId, roomId).stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> searchMessagesInRoom(String keyword, Long roomId) {
        return messageRepository.searchMessagesInRoom(keyword, roomId).stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public Optional<MessageDto> getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .map(MessageDto::new);
    }
    
    public MessageDto updateMessage(Long messageId, String newContent, String username) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Check if user owns the message
        if (!message.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only edit your own messages");
        }
        
        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        
        Message updatedMessage = messageRepository.save(message);
        
        return new MessageDto(updatedMessage);
    }
    
    public void deleteMessage(Long messageId, String username) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Check if user owns the message
        if (!message.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }
    
    public Long getMessageCountByRoomId(Long roomId) {
        return messageRepository.countMessagesByRoomId(roomId);
    }
} 