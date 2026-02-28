package com.vibey.PartPlay.service;

import com.vibey.PartPlay.Entity.Role;
import com.vibey.PartPlay.Entity.Room;
import com.vibey.PartPlay.Entity.RoomParticipants;
import com.vibey.PartPlay.Entity.Users;
import com.vibey.PartPlay.Utils.RandomCodeGenerator;
import com.vibey.PartPlay.repo.RoomParticipantsRepo;
import com.vibey.PartPlay.repo.RoomRepo;
import com.vibey.PartPlay.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoomService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomParticipantsRepo participantsRepo;

    @Autowired
    private RandomCodeGenerator codeGenerator;


    public String createRoom(Long hostId) {

        String code;
        do {
            code = codeGenerator.generateCode();
        } while (roomRepo.existsByRoomCode(code));

        // create room
        Room room = new Room();
        room.setRoomCode(code);
        room.setHostId(hostId);
        room.setCreatedAt(LocalDateTime.now());
        room.setActive(true);

        Room savedRoom = roomRepo.save(room);

        // add host as participant
        RoomParticipants participant = new RoomParticipants();
        participant.setRoomId(savedRoom.getRoomId());
        participant.setUserId(hostId);
        participant.setRole(Role.Host);
        participant.setJoinedAt(LocalDateTime.now());

        participantsRepo.save(participant);

        return code;
    }

    // ── Method 1: Host invites a user by their userId ─────────────────────────
    // Only the host of the room can call this.

    public String hostInviteUser(String roomCode, String hostUsername, String targetUsername){

        Room room = getRoomOrThrow(roomCode);

        Users host = userRepo.findByUsername(hostUsername)
                .orElseThrow(() -> new RuntimeException("Host not found with username: " + hostUsername));


        // Only the host is allowed to invite others
        if (room.getHostId() != host.getUserId()) {
            throw new RuntimeException("Only the host can invite participants");
        }

        if(!room.isActive()){
            throw new RuntimeException("Room is no longer active");
        }

        // Make sure the target user actually exists
        Users target = userRepo.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));

        // avoid duplicate entries
        if (isAlreadyParticipant(room.getRoomId(), target.getUserId())) {
            throw new RuntimeException("User is already in this room");
        }

        addParticipant(room.getRoomId(), target.getUserId(), Role.Member);

        return "User added to room successfully";
    }

    // ── Method 2a: Member joins by entering the 6-char code ──────────────────

    public String joinByCode(String roomCode, String username) {
        Room room = getRoomOrThrow(roomCode);

        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!room.isActive()) {
            throw new RuntimeException("Room is no longer active");
        }

        if (isAlreadyParticipant(room.getRoomId(), user.getUserId())) {
            throw new RuntimeException("You are already in this room");
        }

        addParticipant(room.getRoomId(), user.getUserId(), Role.Member);

        return "Joined room successfully";
    }

    public String joinByLink(String roomCode, String username) {
        // Reuses the same logic — the link just provides the code via URL
        return joinByCode(roomCode, username);
    }


    // Helper methods

    private Room getRoomOrThrow(String roomCode) {
        return roomRepo.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomCode));
    }

    private boolean isAlreadyParticipant(Long roomId, long userId) {
        return participantsRepo.existsByRoomIdAndUserId(roomId, userId);
    }

    private void addParticipant(Long roomId, long userId, Role role) {
        RoomParticipants participant = new RoomParticipants();
        participant.setRoomId(roomId);
        participant.setUserId(userId);
        participant.setRole(role);
        participant.setJoinedAt(LocalDateTime.now());
        participantsRepo.save(participant);
    }
}
