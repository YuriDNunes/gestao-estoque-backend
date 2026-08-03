package com.management.inventory.productAllocation.service;

import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import com.management.inventory.productAllocation.dto.ProductAllocationRequestDTO;
import com.management.inventory.productAllocation.dto.ProductAllocationResponseDTO;
import com.management.inventory.productAllocation.entity.ProductAllocation;
import com.management.inventory.productAllocation.repository.ProductAllocationRepository;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductAllocationService {

    private ProductAllocationRepository repository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    public ProductAllocationService(ProductAllocationRepository repository, ProductRepository productRepository, UserRepository userRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProductAllocationResponseDTO allocate(ProductAllocationRequestDTO request){

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o produto"));

        if (product.getQuantity() < request.getQuantity()) throw new RuntimeException("Quantidade do produto não é suficiente");

        product.setQuantity(product.getQuantity() - request.getQuantity());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o usuário"));

        LocalDateTime now = LocalDateTime.now();

        ProductAllocation productAllocation = new ProductAllocation();

        productAllocation.setAllocatedQuantity(request.getQuantity());
        productAllocation.setAllocationDate(now);
        productAllocation.setProduct(product);
        productAllocation.setUser(user);

        repository.save(productAllocation);

        ProductAllocationResponseDTO response = new ProductAllocationResponseDTO();

        response.setId(productAllocation.getId());
        response.setUsername(productAllocation.getUser().getName());
        response.setProductName(productAllocation.getProduct().getName());
        response.setProductQuantity(productAllocation.getAllocatedQuantity());
        response.setAllocateDate(productAllocation.getAllocationDate());

        return response;
    }

}
