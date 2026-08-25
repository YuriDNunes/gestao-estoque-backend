package com.management.inventory.productAllocation.service;

import com.management.inventory.history.entity.History;
import com.management.inventory.history.repository.HistoryRepository;
import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import com.management.inventory.productAllocation.dto.ProductAllocationRequestDTO;
import com.management.inventory.productAllocation.dto.ProductAllocationResponseDTO;
import com.management.inventory.productAllocation.entity.ProductAllocation;
import com.management.inventory.productAllocation.repository.ProductAllocationRepository;
import com.management.inventory.shared.entity.ActionEnum;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductAllocationService {

    private ProductAllocationRepository repository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private HistoryRepository historyRepository;

    public ProductAllocationService(ProductAllocationRepository repository,
                                    ProductRepository productRepository,
                                    UserRepository userRepository,
                                    HistoryRepository historyRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ProductAllocationResponseDTO allocate(ProductAllocationRequestDTO request){

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o produto"));

        if (product.getQuantity() < request.getQuantity()) throw new RuntimeException("Quantidade do produto não é suficiente");

        product.setQuantity(product.getQuantity() - request.getQuantity());

        User user = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o usuário"));

        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("Gestor autenticado não encontrado"));

        LocalDateTime now = LocalDateTime.now();

        ProductAllocation productAllocation = new ProductAllocation();
        productAllocation.setAllocatedQuantity(request.getQuantity());
        productAllocation.setAllocationDate(now);
        productAllocation.setProduct(product);
        productAllocation.setUser(user);

        repository.save(productAllocation);

        History history = new History();
        history.setDateAction(now);
        history.setProduct(product);
        history.setTargetUser(user);
        history.setManager(manager);
        history.setAction(ActionEnum.ALLOCATION);
        history.setQuantity(request.getQuantity());

        historyRepository.save(history);

        ProductAllocationResponseDTO response = new ProductAllocationResponseDTO();
        response.setId(productAllocation.getId());
        response.setUsername(productAllocation.getUser().getName());
        response.setProductName(productAllocation.getProduct().getName());
        response.setProductQuantity(productAllocation.getAllocatedQuantity());
        response.setAllocateDate(productAllocation.getAllocationDate());

        return response;
    }

    public List<ProductAllocationResponseDTO> listMyAllocations(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ProductAllocation> allocations = repository.findAllByUserEmailOrderByIdDesc(email);

        return allocations.stream().map(allocation -> {
            ProductAllocationResponseDTO dto = new ProductAllocationResponseDTO();

            dto.setId(allocation.getId());
            dto.setUsername(allocation.getUser().getName());
            dto.setProductName(allocation.getProduct().getName());
            dto.setProductQuantity(allocation.getAllocatedQuantity());
            dto.setAllocateDate(allocation.getAllocationDate());

            return dto;
        }).toList();
    }

    @Transactional
    public void returnAllocation(Long allocationId, Integer quantityToReturn) {
        ProductAllocation allocation = repository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Alocação não encontrada"));

        String loggedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allocation.getUser().getEmail().equals(loggedEmail)) {
            throw new RuntimeException("Acesso negado: Você não pode devolver produtos de outro usuário.");
        }

        if (quantityToReturn > allocation.getAllocatedQuantity()) {
            throw new RuntimeException("Quantidade de devolução excede o total alocado.");
        }

        Product product = allocation.getProduct();
        product.setQuantity(product.getQuantity() + quantityToReturn);
        productRepository.save(product);

        int remainingQuantity = allocation.getAllocatedQuantity() - quantityToReturn;

        if (remainingQuantity == 0) {
            repository.delete(allocation);
        } else {
            allocation.setAllocatedQuantity(remainingQuantity);
            repository.save(allocation);
        }
    }
}