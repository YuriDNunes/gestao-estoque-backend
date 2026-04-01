package com.management.inventory.user.controller;

import com.management.inventory.user.dto.UserRequestDTO;
import com.management.inventory.user.dto.UserResponseDTO;
import com.management.inventory.user.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServices services;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO user){
        return services.create(user);
    }

}
