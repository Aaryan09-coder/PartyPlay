package com.vibey.PartPlay.repo;

import com.vibey.PartPlay.Entity.RoomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomParticipantsRepo extends JpaRepository<RoomParticipants, Long> {

    // Used to prevent duplicate participants
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    // Useful for listing everyone in a room
    List<RoomParticipants> findAllByRoomId(Long roomId);
}
