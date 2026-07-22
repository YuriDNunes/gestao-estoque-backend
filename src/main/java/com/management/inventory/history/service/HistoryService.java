package com.management.inventory.history.service;

import com.management.inventory.history.dto.HistoryRequestDTO;
import com.management.inventory.history.dto.HistoryResponseDTO;
import com.management.inventory.history.entity.History;
import com.management.inventory.history.repository.HistoryRepository;
import com.management.inventory.product.entity.Product;
import com.management.inventory.product.repository.ProductRepository;
import com.management.inventory.shared.repository.ActionRepository;
import com.management.inventory.user.entity.User;
import com.management.inventory.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoryService {

    private HistoryRepository historyRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private ActionRepository actionRepository;

    public HistoryService(HistoryRepository historyRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository,
                          ActionRepository actionRepository
    ) {
        this.historyRepository = historyRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public HistoryResponseDTO registerWithdrawal(HistoryRequestDTO request) throws Exception {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Exception("Usuário não encontrado!"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new Exception("Produto não encontrado!"));

        if(request.getQuantity() > product.getQuantity()) throw new Exception("Estoque insuficiente!");

        product.setQuantity(product.getQuantity() - request.getQuantity());

        var action = actionRepository.findById(1L)
                .orElseThrow(() -> new Exception("Ação não encontrada!"));

        History history = new History();

        LocalDateTime now = LocalDateTime.now();

        history.setDateAction(now);
        history.setProduct(product);
        history.setUser(user);
        history.setAction(action);
        history.setQuantity(request.getQuantity());

        history = historyRepository.save(history);

        HistoryResponseDTO response = new HistoryResponseDTO();

        response.setUserName(history.getUser().getName());
        response.setProductName(history.getProduct().getName());
        response.setQuantity(history.getQuantity());
        response.setDateAction(history.getDateAction());
        response.setActionName(history.getAction().getAction());

        return response;
    }
}
