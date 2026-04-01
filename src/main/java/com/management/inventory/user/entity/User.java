package com.management.inventory.user.entity;

import com.management.inventory.auth.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user")
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 45, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 45, unique = true, nullable = false)
    private String register;

    @Column(nullable = false)
    private Boolean access;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
