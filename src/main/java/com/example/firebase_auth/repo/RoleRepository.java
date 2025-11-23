package com.example.firebase_auth.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.firebase_auth.enums.RoleEnum;
import com.example.firebase_auth.model.Role;

@Repository
public interface RoleRepository  extends CrudRepository<Role, Integer>  {
   Role findByRole(RoleEnum role);
}