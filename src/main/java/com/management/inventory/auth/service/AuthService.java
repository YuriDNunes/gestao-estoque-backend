package com.management.inventory.auth.service;

import com.management.inventory.auth.dto.AccountCredentialsDTO;
import com.management.inventory.security.jwt.JwtTokenProvider;
import com.management.inventory.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    public ResponseEntity<?> signIn(AccountCredentialsDTO credentials){

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    credentials.getUsername(),
                    credentials.getPassword()
            )
        );

        var user = repository.findByEmail(credentials.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Email " + credentials.getUsername() + " not found!"));

        var tokenResponse = tokenProvider.generateToken(
                credentials.getUsername(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        return ResponseEntity.ok(tokenResponse);
    }

}
