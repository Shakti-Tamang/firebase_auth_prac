package com.example.firebase_auth.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterUserDto {
    private String name;
    private String email;
    private String contactNumber;
    private String role;
    private String password;
}