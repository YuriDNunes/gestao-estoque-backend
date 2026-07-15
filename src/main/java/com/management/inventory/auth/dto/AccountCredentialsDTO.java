package com.management.inventory.auth.dto;

import java.util.Objects;

public class AccountCredentialsDTO {

    private String username;
    private String passsword;

    public AccountCredentialsDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasssword() {
        return passsword;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountCredentialsDTO that)) return false;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPasssword(), that.getPasssword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPasssword());
    }
}
