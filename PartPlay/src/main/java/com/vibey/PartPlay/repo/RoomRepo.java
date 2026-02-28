package com.vibey.PartPlay.repo;

import com.vibey.PartPlay.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepo extends JpaRepository<Room, Long> {
    boolean existsByRoomCode(String roomCode);

    Optional<Room> findByRoomCode(String roomCode);
}
