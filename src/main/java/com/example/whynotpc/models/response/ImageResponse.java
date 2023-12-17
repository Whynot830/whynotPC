package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ImageDTO;
import com.example.whynotpc.models.img.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ImageResponse extends BasicResponse {
    @JsonProperty
    List<ImageDTO> images;

    private ImageDTO toDto(Image image) {
        return new ImageDTO(image.getId(), image.getName(), image.getType());
    }

    public ImageResponse(int statusCode, List<Image> images) {
        super(statusCode);
        this.images = images.stream().map(this::toDto).toList();
    }

    public ImageResponse(int statusCode, Image image) {
        super(statusCode);
        this.images = Collections.singletonList(toDto(image));
    }

    public ImageResponse(int statusCode) {
        this(statusCode, Collections.emptyList());
    }
}
