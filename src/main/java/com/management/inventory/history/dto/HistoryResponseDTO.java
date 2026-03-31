package com.management.inventory.history.dto;

import com.management.inventory.product.entity.Product;
import com.management.inventory.shared.entity.Action;
import com.management.inventory.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryResponseDTO {

    private Long id;
    private LocalDateTime dateAction;
    private Product product;
    private Action action;
    private User user;

}
