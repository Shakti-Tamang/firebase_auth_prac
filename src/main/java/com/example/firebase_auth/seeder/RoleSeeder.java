package com.example.firebase_auth.seeder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.example.firebase_auth.enums.RoleEnum;
import com.example.firebase_auth.model.Role;
import com.example.firebase_auth.repo.RoleRepository;


@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }
    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.ADMIN,
                RoleEnum.USER };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.ADMIN, "Administrator role",
                RoleEnum.USER, "user role");
        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = Optional.ofNullable(roleRepository.findByRole(roleName));
            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();
                roleToCreate.setRole(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));
                roleRepository.save(roleToCreate);
            });
        });
    }
}