package com.management.inventory.history.entity;

import com.management.inventory.product.entity.Product;
import com.management.inventory.shared.entity.Action;
import com.management.inventory.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateAction;

    @ManyToOne
    @JoinColumn(name = "product_idproduct", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "action_idaction", nullable = false)
    private Action action;

    @ManyToOne
    @JoinColumn(name = "user_iduser", nullable = false)
    private User user;

}
