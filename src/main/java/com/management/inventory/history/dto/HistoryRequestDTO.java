package com.management.inventory.history.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HistoryRequestDTO {

    private Long userId;
    private Long productId;
    private Integer quantity;

}
