package com.example.whynotpc.models.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Integer id,
        String title,
        BigDecimal price,
        String imgName,
        String category
) {
}
