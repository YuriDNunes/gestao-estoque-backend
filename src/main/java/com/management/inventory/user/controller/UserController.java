package com.management.inventory.user.controller;

import com.management.inventory.user.dto.UserRequestDTO;
import com.management.inventory.user.dto.UserResponseDTO;
import com.management.inventory.user.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServices services;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO user){
        return services.create(user);
    }

    @GetMapping
    public List<UserResponseDTO> listUsers(){
        return services.listUsers();
    }

}
