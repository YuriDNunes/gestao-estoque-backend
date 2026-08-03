package com.management.inventory.productAllocation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductAllocationRequestDTO {

    private Long userId;
    private Long productId;
    private Integer quantity;

}
