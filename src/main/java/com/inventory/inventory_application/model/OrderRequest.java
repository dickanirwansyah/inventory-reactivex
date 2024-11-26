package com.inventory.inventory_application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Long id;
    private String orderNo;
    private Long itemId;
    private Integer qty;
    private BigDecimal price;

}
