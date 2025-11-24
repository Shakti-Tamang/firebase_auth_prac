package com.example.firebase_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.firebase_auth.ApiResponse.ApiResponse;
import com.example.firebase_auth.dto.RegisterUserDto;
import com.example.firebase_auth.model.UserModel;
import com.example.firebase_auth.repo.UserReposiitory;
import com.example.firebase_auth.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth-service")

public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserReposiitory userReposiitory;

    public AuthenticationController(AuthenticationService authenticationService, UserReposiitory userReposiitory) {
        this.authenticationService = authenticationService;
        this.userReposiitory = userReposiitory;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> postMethodName(@RequestBody RegisterUserDto registerTeacherDto) {
        try {
            UserModel userModel = userReposiitory.findByEmail(registerTeacherDto.getEmail());
            System.out.println(
                    "Resandmdnfjjsxbasjkjdbascx cdx kjnbacksc jscbd dazx c b cxbz :" + registerTeacherDto.getName()
                            + registerTeacherDto.getRole());
            if (userModel == null) {
                authenticationService.register(registerTeacherDto);
                ApiResponse apiResponse = ApiResponse.builder().message("User registered successfully!")
                        .statusCode(HttpStatus.OK.value()).build();
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            } else {
                ApiResponse apiResponse = ApiResponse.builder().message("User with email already exist")
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
            }

        } catch (Exception ex) {
            ApiResponse apiResponse = ApiResponse.builder().message("Failed: " + ex.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);

        }

    }

}