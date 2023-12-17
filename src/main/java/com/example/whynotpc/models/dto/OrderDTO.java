package com.example.whynotpc.models.dto;

import com.example.whynotpc.models.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        Integer id,
        String status,
        BigDecimal total,
        List<OrderItem> items,
        Integer userId
) {
}
