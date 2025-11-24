package com.example.firebase_auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.firebase_auth.model.UserModel;
import com.example.firebase_auth.repo.UserReposiitory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    
    private final UserReposiitory userReposiitory; 

        @GetMapping("/getAdmin")
    public String getAllFind(){

        
        return "success hlo";
    }

          @GetMapping("/getAdmin")
    public UserModel testGet(@RequestParam("id") int id){

        UserModel user=userReposiitory.findById(id).orElse(null);
        
        return user;
    }

}
