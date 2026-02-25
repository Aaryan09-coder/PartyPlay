package com.vibey.PartPlay.service;

import com.vibey.PartPlay.Entity.ProfilePhoto;
import com.vibey.PartPlay.Entity.Users;
import com.vibey.PartPlay.configuration.ApiResponse;
import com.vibey.PartPlay.dtos.requests.CreateUserRequest;
import com.vibey.PartPlay.dtos.response.UserResponseDTO;
import com.vibey.PartPlay.repo.PhotoRepo;
import com.vibey.PartPlay.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PhotoRepo photoRepo;

    @Autowired
    private PasswordEncoder encoder;

    public String createUser(CreateUserRequest requestDto, MultipartFile file) {
        try{
            Users user = new Users();
            user.setUsername(requestDto.getUsername());
            user.setName(requestDto.getName());
            user.setEmail(requestDto.getEmail());
            user.setPassword(encoder.encode(requestDto.getPassword()));
            user.setAge(requestDto.getAge());

            user = userRepo.save(user);

            // Check if file is there and save it to mongoDB
            if(file != null && !file.isEmpty()){

                if(!file.getContentType().startsWith("image/")){
                    throw new RuntimeException("Only image files allowed");
                }

                ProfilePhoto pic = new ProfilePhoto();
                pic.setUserId(user.getUserId());
                pic.setContentType(file.getContentType());
                pic.setImageData(file.getBytes());

                ProfilePhoto savedPic = photoRepo.save(pic);

                // save pic referenceId in relational database.
                user.setProfilePhotoId(savedPic.getId());
                userRepo.save(user);
            }

            return "User created successfully";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UserResponseDTO userProfile(@PathVariable String username){
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user == null){
            throw new RuntimeException("User Not found");
        }

        return mapToDTO(user);
    }

    public UserResponseDTO mapToDTO(Users user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setAge(user.getAge());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        // attach profile photo URL
        if(user.getProfilePhotoId() != null){
            dto.setProfilePhotoUrl(
                    "http://localhost:8080/users/photo/" + user.getProfilePhotoId()
            );
        }

        return dto;
    }


    public ProfilePhoto getUserPhoto(String photoId){
        ProfilePhoto photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        return photo;
    }


}
