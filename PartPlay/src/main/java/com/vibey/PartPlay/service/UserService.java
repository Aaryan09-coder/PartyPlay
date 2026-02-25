package com.vibey.PartPlay.service;

import com.vibey.PartPlay.Entity.ProfilePhoto;
import com.vibey.PartPlay.Entity.Users;
import com.vibey.PartPlay.dtos.requests.CreateUserRequest;
import com.vibey.PartPlay.repo.PhotoRepo;
import com.vibey.PartPlay.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PhotoRepo photoRepo;

    public String createUser(CreateUserRequest requestDto, MultipartFile file) {
        try{
            Users user = new Users();
            user.setUsername(requestDto.getUsername());
            user.setName(requestDto.getName());
            user.setEmail(requestDto.getEmail());
            user.setPassword(requestDto.getPassword());
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


}
