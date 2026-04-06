package com.management.inventory.product.controller;

import com.management.inventory.product.dto.ProductRequestDTO;
import com.management.inventory.product.dto.ProductResponseDTO;
import com.management.inventory.product.service.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductServices services;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO product){
        return ResponseEntity.status(201).body(services.createProduct(product));
    }

}
