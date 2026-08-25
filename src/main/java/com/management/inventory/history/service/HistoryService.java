package com.management.inventory.history.service;

import com.management.inventory.history.dto.HistoryRequestDTO;
import com.management.inventory.history.dto.HistoryResponseDTO;
import com.management.inventory.history.entity.History;
import com.management.inventory.history.repository.HistoryRepository;
import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import com.management.inventory.shared.entity.ActionEnum;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private HistoryRepository historyRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    public HistoryService(HistoryRepository historyRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public HistoryResponseDTO registerAllocation(HistoryRequestDTO request) throws Exception {

        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new Exception("Gestor autenticado não encontrado no sistema!"));

        User targetUser = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new Exception("Usuário alvo não encontrado!"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new Exception("Produto não encontrado!"));

        if(request.getQuantity() > product.getQuantity()) throw new Exception("Estoque insuficiente!");

        product.setQuantity(product.getQuantity() - request.getQuantity());

        History history = new History();
        history.setDateAction(LocalDateTime.now());
        history.setProduct(product);
        history.setTargetUser(targetUser);
        history.setManager(manager);
        history.setAction(ActionEnum.ALLOCATION);
        history.setQuantity(request.getQuantity());

        history = historyRepository.save(history);
        return buildResponseDTO(history);
    }

    @Transactional
    public HistoryResponseDTO registerReturn(HistoryRequestDTO request) throws Exception {
        User targetUser = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new Exception("Usuário alvo não encontrado!"));

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new Exception("Gestor não encontrado!"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new Exception("Produto não encontrado!"));

        product.setQuantity(request.getQuantity() + product.getQuantity());

        History history = new History();
        history.setDateAction(LocalDateTime.now());
        history.setProduct(product);
        history.setTargetUser(targetUser);
        history.setManager(manager);
        history.setAction(ActionEnum.RETURN);
        history.setQuantity(request.getQuantity());

        history = historyRepository.save(history);
        return buildResponseDTO(history);
    }

    public List<HistoryResponseDTO> getAllHistories() {
        return historyRepository.findAllByOrderByDateActionDesc().stream()
                .map(this::buildResponseDTO)
                .collect(Collectors.toList());
    }

    private HistoryResponseDTO buildResponseDTO(History history) {
        HistoryResponseDTO response = new HistoryResponseDTO();
        response.setId(history.getId());
        response.setTargetUserName(history.getTargetUser().getName());
        response.setManagerName(history.getManager().getName());
        response.setProductName(history.getProduct().getName());
        response.setQuantity(history.getQuantity());
        response.setDateAction(history.getDateAction());
        response.setActionName(history.getAction().name());
        return response;
    }
}