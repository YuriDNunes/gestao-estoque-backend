package com.management.inventory.user.service;

import java.util.List;

import com.management.inventory.shared.utils.PasswordGeneratorUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.management.inventory.auth.entity.Role;
import com.management.inventory.auth.repository.RoleRepository;
import com.management.inventory.user.dto.UserRequestDTO;
import com.management.inventory.user.dto.UserResponseDTO;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;

@Service
public class UserServices implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(UserServices.class.getName());

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository repositoryRole;

    private PasswordEncoder passwordEncoder;

    private JavaMailSender mailSender;

    public UserServices(PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO user){
        logger.info("Creating one user");

        var generatedPassword = PasswordGeneratorUtil.generateRandomPassword();

        user.setPassword(generatedPassword);

        var entity = toEntity(user);

        var dto = toDTO(repository.save(entity));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(dto.getEmail());
        mailMessage.setSubject("Bem-vindo! Suas credenciais de acesso");
        mailMessage.setText("Sua senha para entrar no sistema é: " + generatedPassword);

        mailSender.send(mailMessage);

        return dto;
    }

    public List<UserResponseDTO> listUsers(String role){
        logger.info("Listing all users");
        var users = toDTOList(repository.findByRole_RoleAndIsDeletedFalse(role));

        return users;
    }

    @Transactional
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

    @Transactional
    public void deleteUser(Long id){
        logger.info("Deleting one user");

        User entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Transactional
    public UserResponseDTO updateAccess(Long id, Boolean access){
        logger.info("Updating user access");

        var entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setAccess(access);
        repository.save(entity);
        return toDTO(entity);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    private User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
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

}
