package com.example.whynotpc.models.dto;

public record ProductDTO(
        Integer id,
        String title,
        Float price,
        String imgName,
        String category
) {
}
