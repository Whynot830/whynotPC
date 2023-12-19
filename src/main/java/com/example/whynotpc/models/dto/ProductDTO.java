package com.example.whynotpc.models.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String title,
        BigDecimal price,
        String imgName,
        String category
) {
}
