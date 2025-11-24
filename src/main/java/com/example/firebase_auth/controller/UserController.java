package com.example.firebase_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.firebase_auth.ApiResponse.ApiResponse;
import com.example.firebase_auth.model.UserModel;
import com.example.firebase_auth.repo.UserReposiitory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserReposiitory userReposiitory; 

    @GetMapping("/getAll")
    public String getAllFind(){


        return "success hlo";
    }

    @GetMapping("/loggedInUser")
    public ResponseEntity<ApiResponse>getLoogedInUser(){

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        UserModel model=userReposiitory.findByEmail(authentication.getName());

        ApiResponse apiResponse=ApiResponse.<UserModel>builder().message("sucessfully logged in user").statusCode(HttpStatus.OK.value()).date(model).build();

        return  ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }



}
