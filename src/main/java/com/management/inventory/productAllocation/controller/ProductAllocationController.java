package com.management.inventory.productAllocation.controller;

import com.management.inventory.productAllocation.dto.ProductAllocationRequestDTO;
import com.management.inventory.productAllocation.dto.ProductAllocationResponseDTO;
import com.management.inventory.productAllocation.service.ProductAllocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProductAllocationResponseDTO>> listMyAllocations(){
        return ResponseEntity.status(HttpStatus.OK).body(service.listMyAllocations());
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Void> returnAllocation(
            @PathVariable("id") Long id,
            @RequestBody ProductAllocationRequestDTO request
    ) {
        service.returnAllocation(id, request.getQuantity());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
