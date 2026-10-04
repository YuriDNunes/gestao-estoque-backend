package com.management.inventory.productAllocation.repository;

import com.management.inventory.product.entity.Product;
import com.management.inventory.productAllocation.entity.ProductAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAllocationRepository extends JpaRepository<ProductAllocation, Long> {

    List<ProductAllocation> findAllByUserEmailOrderByIdDesc(String email);

    boolean existsByProduct(Product product);

}
