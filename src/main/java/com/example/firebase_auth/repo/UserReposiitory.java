package com.example.firebase_auth.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.firebase_auth.model.UserModel;



@Repository
public interface UserReposiitory extends JpaRepository<UserModel, Integer> {
    UserModel findByEmail(String email);
}
