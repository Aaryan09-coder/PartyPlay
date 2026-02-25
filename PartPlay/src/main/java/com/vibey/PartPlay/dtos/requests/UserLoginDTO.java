package com.vibey.PartPlay.dtos.requests;


import jakarta.validation.constraints.NotBlank;

public class UserLoginDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // getters
    public @NotBlank String getUsername() {
        return username;
    }

    public @NotBlank String getPassword() {
        return password;
    }


    // setters
    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
