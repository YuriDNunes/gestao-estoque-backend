package com.management.inventory.product.service;

import com.management.inventory.product.dto.ProductRequestDTO;
import com.management.inventory.product.dto.ProductResponseDTO;
import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServices {

    private Logger logger = LoggerFactory.getLogger(ProductServices.class.getName());

    @Autowired
    private ProductRepository repository;

    public ProductResponseDTO createProduct(ProductRequestDTO product){
        logger.info("Creating one product");

        var entity = toEntity(product);

        var dto = toDTO(repository.save(entity));

        return dto;
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO product){
        logger.info("Updating one product");

        var entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        entity.setCode(product.getCode());
        entity.setName(product.getName());
        entity.setQuantity(product.getQuantity());

        repository.save(entity);

        return toDTO(entity);
    }

    public List<ProductResponseDTO> listAllProducts(){
        logger.info("Listing all products");

        return toDTOList(repository.findAll());
    }

    private Product toEntity(ProductRequestDTO dto){
        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setQuantity(dto.getQuantity());

        return product;
    }

    private ProductResponseDTO toDTO(Product product){
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setQuantity(product.getQuantity());

        return dto;
    }

    public List<ProductResponseDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(this::toDTO)
                .toList();
    }

}
