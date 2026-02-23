package com.vibey.PartPlay.dtos.requests;
import jakarta.validation.constraints.*;

public class CreateUserRequest {

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    private String password;

    // optional
    private String name;

    @Min(1)
    private int age;

    @NotBlank
    private String email;

    // getters and setters

    public @NotBlank @Size(max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(max = 50) String username) {
        this.username = username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Min(1)
    public int getAge() {
        return age;
    }

    public void setAge(@Min(1) int age) {
        this.age = age;
    }

    public @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank String email) {
        this.email = email;
    }
}
