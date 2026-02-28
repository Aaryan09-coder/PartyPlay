package com.vibey.PartPlay.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RoomParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private com.vibey.PartPlay.Entity.Role role;// Host & Member

    private LocalDateTime joinedAt;


    // getter

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getUserId() {
        return userId;
    }


    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public Role getRole() {
        return role;
    }

    // setter

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
