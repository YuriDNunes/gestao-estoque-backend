package com.management.inventory.product.controller;

import com.management.inventory.product.dto.ProductRequestDTO;
import com.management.inventory.product.dto.ProductResponseDTO;
import com.management.inventory.product.service.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping(value = "/{id}")
    public ProductResponseDTO updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO product){
        return services.updateProduct(id, product);
    }

    @GetMapping
    public List<ProductResponseDTO> listAllProducts(){
        return services.listAllProducts();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        services.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

}
