package com.example.whynotpc.models.dto;

import com.example.whynotpc.models.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * A data transfer object (DTO) representing user's order.
 */
public record OrderDTO(
        Long id,
        String status,
        BigDecimal total,
        List<OrderItem> items,
        Long userId
) {
}
