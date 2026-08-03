package com.management.inventory.productAllocation.repository;

import com.management.inventory.productAllocation.entity.ProductAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAllocationRepository extends JpaRepository<ProductAllocation, Long> {
}
