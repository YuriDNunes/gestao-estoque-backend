package com.management.inventory.user.controller;

import com.management.inventory.user.dto.UserRequestDTO;
import com.management.inventory.user.dto.UserResponseDTO;
import com.management.inventory.user.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServices services;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO user) {
        return ResponseEntity.status(201).body(services.create(user));
    }

    @GetMapping()
    public List<UserResponseDTO> listUsers(@RequestParam String role) {
        return services.listUsers(role);
    }


    @PutMapping(value = "/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO user) {
        return services.updateUser(id, user);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        services.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/access")
    public ResponseEntity<UserResponseDTO> updateAccess(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body
            ){
        return ResponseEntity.ok(services.updateAccess(id, body.get("access")));
    }

}
