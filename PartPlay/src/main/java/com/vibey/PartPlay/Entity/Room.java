package com.vibey.PartPlay.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    @Column(unique = true, nullable = false)
    private String roomCode;

    private long hostId;

    private boolean isActive = true;

    private LocalDateTime createdAt;


    // Getters

    public long getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }


    public long getHostId() {
        return hostId;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // setters

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
