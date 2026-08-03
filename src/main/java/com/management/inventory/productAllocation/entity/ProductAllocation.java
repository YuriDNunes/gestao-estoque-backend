package com.management.inventory.productAllocation.entity;

import com.management.inventory.product.entity.Product;
import com.management.inventory.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_allocation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "allocated_quantity")
    private Integer allocatedQuantity;

    @Column(nullable = false, name = "allocation_date")
    private LocalDateTime allocationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
