package com.management.inventory.history.dto;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HistoryResponseDTO {
    private Long id;
    private String targetUserName;
    private String managerName;
    private String productName;
    private Integer quantity;
    private LocalDateTime dateAction;
    private String actionName;
}