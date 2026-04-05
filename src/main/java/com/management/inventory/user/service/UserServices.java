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

import java.util.List;

@Service
public class UserServices {

    private Logger logger = LoggerFactory.getLogger(UserServices.class.getName());

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository repositoryRole;

    public UserResponseDTO create(UserRequestDTO user){
        logger.info("Creating one user");
        var entity = toEntity(user);

        var dto = toDTO(repository.save(entity));


        return dto;
    }

    public List<UserResponseDTO> listUsers(){
        logger.info("Listing all users");
        var users = toDTOList(repository.findAll());

        return users;
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO user){
        logger.info("Updating one user");

        var entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setRegister(user.getRegister());
        entity.setAccess(user.getAccess());

        Role role = repositoryRole.findByRole(user.getRole());
        entity.setRole(role);

        repository.save(entity);

        return toDTO(entity);
    }

    public void deleteUser(Long id){
        logger.info("Deleting one user");

        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        repository.delete(entity);
    }

    private User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRegister(dto.getRegister());
        user.setAccess(dto.getAccess());

        Role role = repositoryRole.findByRole((dto.getRole()));
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

    public List<UserResponseDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<User> toEntityList(List<UserRequestDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }

}
