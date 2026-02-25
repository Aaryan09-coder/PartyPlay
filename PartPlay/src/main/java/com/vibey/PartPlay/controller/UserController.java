package com.vibey.PartPlay.controller;

import com.vibey.PartPlay.Entity.ProfilePhoto;
import com.vibey.PartPlay.configuration.ApiResponse;
import com.vibey.PartPlay.dtos.requests.CreateUserRequest;
import com.vibey.PartPlay.dtos.response.UserResponseDTO;
import com.vibey.PartPlay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String createUser(@RequestPart("user") CreateUserRequest requestDto,
                             @RequestPart(value = "file", required = false)MultipartFile file) throws IOException
    {
        return userService.createUser(requestDto, file);
    }

    @GetMapping("users/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> userProfile(@PathVariable String username){

        try{
            UserResponseDTO dto = userService.userProfile(username);

            return ResponseEntity.ok(new ApiResponse<>(false, "User profile fetched", dto));

        }
        catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, ex.getMessage(), null));
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Something went wrong", null));
        }
    }

    @GetMapping("/users/photo/{photoId}")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable String photoId){
        ProfilePhoto photo = userService.getUserPhoto(photoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getImageData());
    }
}
