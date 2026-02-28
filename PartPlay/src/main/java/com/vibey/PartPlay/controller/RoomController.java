package com.vibey.PartPlay.controller;

import com.vibey.PartPlay.Security.UserPrincipal;
import com.vibey.PartPlay.configuration.ApiResponse;
import com.vibey.PartPlay.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createRoom(Authentication authentication) {

        try{
            Long hostId = extractUserId(authentication);

            String roomCode = roomService.createRoom(hostId);

            return ResponseEntity.ok(new ApiResponse<>(true, "Room created", roomCode));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Room could not be created", null));
        }
    }

    // ── Method 1: Host invites a specific user ────────────────────────────────
    // POST /rooms/{roomCode}/invite/{userId}
    // Only the host of that room can call this.
    // The host looks up a user's profile, gets their userId, and invites them directly.

    @PostMapping("/{roomCode}/invite/{username}")
    public ResponseEntity<ApiResponse<String>> inviteUser(
            @PathVariable String roomCode,
            @PathVariable String username,
            Authentication authentication){

        try{
            String hostUsername = extractUserName(authentication);
            String result = roomService.hostInviteUser(roomCode, hostUsername, username);
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ── Method 2a: Member joins by submitting the code ────────────────────────
    // POST /rooms/join
    // Body param: roomCode (sent as a request param from a form/frontend input)
    // Any logged-in user can call this with a valid code.

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<String>> joinByCode(
            @RequestParam String roomCode,
            Authentication authentication) {
        try {
            String username = extractUserName(authentication);
            String result = roomService.joinByCode(roomCode, username);
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    // ── Method 2b: Member joins via a shareable link ──────────────────────────
    // GET /rooms/join/{roomCode}
    // The host shares a link like: https://yourapp.com/rooms/join/ABC123
    // When the logged-in user clicks it, they are added to the room automatically.
    // GET is used intentionally here so the link is clickable directly from a browser.

    @GetMapping("/join/{roomCode}")
    public ResponseEntity<ApiResponse<String>> joinByLink(
            @PathVariable String roomCode,
            Authentication authentication) {
        try {
            String username = extractUserName(authentication);
            String result = roomService.joinByLink(roomCode, username);
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String extractUserName(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        return username;
    }
    private Long extractUserId(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        long userId = userPrincipal.getUserId();

        return userId;
    }
}
