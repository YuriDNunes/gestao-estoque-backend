package com.management.inventory.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String register;
    private Boolean access;
    private String role;

}
