package com.example.whynotpc.models.dto;

import java.math.BigDecimal;

/**
 * A data transfer object (DTO) representing a product.
 */
public record ProductDTO(
        Long id,
        String title,
        BigDecimal price,
        String imgName,
        String category
) {
}
