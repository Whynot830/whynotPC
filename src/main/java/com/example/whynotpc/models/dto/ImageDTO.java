package com.example.whynotpc.models.dto;

/**
 * A data transfer object (DTO) representing an image.
 */
public record ImageDTO(
        Long id,
        String name,
        String type
) {
}