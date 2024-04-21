package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ImageDTO;
import com.example.whynotpc.models.img.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Represents a response for image-related operations.
 * Extends BasicResponse class.
 */
public class ImageResponse extends BasicResponse {
    /**
     * The list of image DTOs included in the response.
     */
    @JsonProperty
    List<ImageDTO> images;

    /**
     * Converts an Image object to its corresponding DTO representation.
     * @param image The Image object to convert.
     * @return ImageDTO representation of the Image object.
     */
    private ImageDTO toDto(Image image) {
        return new ImageDTO(image.getId(), image.getName(), image.getType());
    }

    /**
     * Constructs a new ImageResponse with the given status code and list of images.
     * @param statusCode The HTTP status code of the response.
     * @param images The list of images to include in the response.
     */
    public ImageResponse(int statusCode, List<Image> images) {
        super(statusCode);
        this.images = images.stream().map(this::toDto).toList();
    }

    /**
     * Constructs a new ImageResponse with the given status code and single image.
     * @param statusCode The HTTP status code of the response.
     * @param image The image to include in the response.
     */
    public ImageResponse(int statusCode, Image image) {
        super(statusCode);
        this.images = Collections.singletonList(toDto(image));
    }

    /**
     * Constructs a new ImageResponse with the given status code and an empty list of images.
     * @param statusCode The HTTP status code of the response.
     */
    public ImageResponse(int statusCode) {
        super(statusCode);
        this.images = Collections.emptyList();
    }

    /**
     * Factory method to create an ImageResponse with HTTP status code 200 (OK) and the given list of images.
     * @param images The list of images to include in the response.
     * @return ImageResponse with status code 200 and the given list of images.
     */
    public static ImageResponse ok(List<Image> images) {
        return new ImageResponse(200, images);
    }

    /**
     * Factory method to create an ImageResponse with HTTP status code 200 (OK) and the given image.
     * @param image The image to include in the response.
     * @return ImageResponse with status code 200 and the given image.
     */
    public static ImageResponse ok(Image image) {
        return new ImageResponse(200, image);
    }

    /**
     * Factory method to create an ImageResponse with HTTP status code 201 (Created) and the given list of images.
     * @param images The list of images to include in the response.
     * @return ImageResponse with status code 201 and the given list of images.
     */
    public static ImageResponse created(List<Image> images) {
        return new ImageResponse(201, images);

    }

    /**
     * Factory method to create an ImageResponse with HTTP status code 201 (Created) and the given image.
     * @param image The image to include in the response.
     * @return ImageResponse with status code 201 and the given image.
     */
    public static ImageResponse created(Image image) {
        return new ImageResponse(201, image);

    }
}
