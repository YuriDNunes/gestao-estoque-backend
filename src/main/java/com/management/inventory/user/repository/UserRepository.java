package com.management.inventory.user.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.management.inventory.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole_RoleAndIsDeletedFalse(String role, Sort sort);
    Optional<User> findByEmail(String email);
}
