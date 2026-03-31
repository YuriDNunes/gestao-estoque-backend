package com.management.inventory.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    private String name;
    private String email;
    private String password;
    private String register;
    private Boolean access;
    private String role;

}
