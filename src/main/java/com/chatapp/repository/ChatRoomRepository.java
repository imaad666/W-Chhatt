package com.chatapp.repository;

import com.chatapp.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByName(String name);
    
    List<ChatRoom> findByIsPrivateFalse();
    
    List<ChatRoom> findByCreatedById(Long createdById);
    
    @Query("SELECT r FROM ChatRoom r JOIN r.participants p WHERE p.id = :userId")
    List<ChatRoom> findRoomsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM ChatRoom r WHERE r.name LIKE %:keyword% OR r.description LIKE %:keyword%")
    List<ChatRoom> findByNameOrDescriptionContaining(@Param("keyword") String keyword);
    
    boolean existsByName(String name);
} 