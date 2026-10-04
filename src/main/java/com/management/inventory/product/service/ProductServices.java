package com.management.inventory.product.service;

import com.management.inventory.exceptions.DuplicateResourceException;
import com.management.inventory.exceptions.InsufficientStockException;
import com.management.inventory.exceptions.ResourceInUseException;
import com.management.inventory.exceptions.ResourceNotFoundException;
import com.management.inventory.product.dto.ProductRequestDTO;
import com.management.inventory.product.dto.ProductResponseDTO;
import com.management.inventory.product.dto.ProductStockRequestDTO;
import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import com.management.inventory.productAllocation.repository.ProductAllocationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServices {

    private Logger logger = LoggerFactory.getLogger(ProductServices.class.getName());

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductAllocationRepository productAllocationRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO product){
        logger.info("Creating one product");

        var entity = toEntity(product);

        if (repository.existsByCode(product.getCode())) throw new DuplicateResourceException("Já existe um produto com esse código: " + product.getCode());

        if (repository.existsByName(product.getName())) throw new DuplicateResourceException("Já existe um produto com esse nome: " + product.getName());

        var dto = toDTO(repository.save(entity));

        return dto;
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO product){
        logger.info("Updating one product");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto: " + product.getName() + " não encontrado"));

        if (repository.existsByCode(entity.getCode()) || repository.existsByName(entity.getName())){
            throw new DuplicateResourceException("Já exista um produto com esse nome ou código");
        }

        entity.setCode(product.getCode());
        entity.setName(product.getName());
        entity.setQuantity(product.getQuantity());

        repository.save(entity);

        return toDTO(entity);
    }

    public List<ProductResponseDTO> listAllProducts(){
        logger.info("Listing all products");

        return toDTOList(repository.findAll(Sort.by(Sort.Direction.ASC, "code")));
    }

    @Transactional
    public void deleteProduct(Long id){
        logger.info("Deleting one product");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (productAllocationRepository.existsByProduct(entity)){
            throw new ResourceInUseException("Não é possivel excluir produto que esta em uso");
        }

        repository.delete(entity);
    }

    @Transactional
    public ProductResponseDTO stockAction(Long id, ProductStockRequestDTO request){

        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possivel encontrar o produto."));

        if ((product.getQuantity() + request.getQuantity()) < 0) throw new InsufficientStockException("Estoque insuficiente.");

        product.setQuantity(product.getQuantity() + request.getQuantity());

        repository.save(product);

        ProductResponseDTO dto = toDTO(product);

        return dto;
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
