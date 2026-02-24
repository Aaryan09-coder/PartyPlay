package com.vibey.PartPlay.controller;

import com.vibey.PartPlay.dtos.requests.CreateUserRequest;
import com.vibey.PartPlay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public String createUser(@RequestPart("user") CreateUserRequest requestDto,
                             @RequestPart(value = "file", required = false)MultipartFile file) throws IOException
    {
        return userService.createUser(requestDto, file);
    }
}
