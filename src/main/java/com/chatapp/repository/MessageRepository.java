package com.chatapp.repository;

import com.chatapp.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    
    List<Message> findByRoomIdOrderByCreatedAtAsc(Long roomId);
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.createdAt > :since ORDER BY m.createdAt ASC")
    List<Message> findNewMessagesByRoomId(@Param("roomId") Long roomId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.room.id = :roomId")
    Long countMessagesByRoomId(@Param("roomId") Long roomId);
    
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.user.id = :userId AND m.room.id = :roomId ORDER BY m.createdAt DESC")
    List<Message> findUserMessagesInRoom(@Param("userId") Long userId, @Param("roomId") Long roomId);
    
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:keyword% AND m.room.id = :roomId ORDER BY m.createdAt DESC")
    List<Message> searchMessagesInRoom(@Param("keyword") String keyword, @Param("roomId") Long roomId);
} 