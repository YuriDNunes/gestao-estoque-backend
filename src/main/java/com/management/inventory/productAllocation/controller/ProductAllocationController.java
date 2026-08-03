package com.management.inventory.productAllocation.controller;

import com.management.inventory.productAllocation.dto.ProductAllocationRequestDTO;
import com.management.inventory.productAllocation.dto.ProductAllocationResponseDTO;
import com.management.inventory.productAllocation.service.ProductAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/allocation")
public class ProductAllocationController {

    private ProductAllocationService service;

    public ProductAllocationController(ProductAllocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductAllocationResponseDTO> allocate(@RequestBody ProductAllocationRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.allocate(request));
    }

}
