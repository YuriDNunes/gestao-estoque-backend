package com.management.inventory.product.controller;

import com.management.inventory.product.dto.ProductRequestDTO;
import com.management.inventory.product.dto.ProductResponseDTO;
import com.management.inventory.product.service.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductServices services;

    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO product){
        return services.createProduct(product);
    }

}
