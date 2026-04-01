package com.management.inventory.user.service;

import com.management.inventory.auth.entity.Role;
import com.management.inventory.auth.repository.RoleRepository;
import com.management.inventory.user.dto.UserRequestDTO;
import com.management.inventory.user.dto.UserResponseDTO;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    private Logger logger = LoggerFactory.getLogger(UserServices.class.getName());

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository repositoryRole;

    public UserResponseDTO create(UserRequestDTO user){
        var entity = toEntity(user);

        var dto = toDTO(repository.save(entity));
        logger.info("Creating one user");

        return dto;
    }

    private User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRegister(dto.getRegister());
        user.setAccess(dto.getAccess());

        Role role = repositoryRole.findById(Long.valueOf(dto.getRole()))
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);

        return user;
    }

    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRegister(user.getRegister());
        dto.setAccess(user.getAccess());
        dto.setRole(user.getRole().getRole());
        return dto;
    }

}
