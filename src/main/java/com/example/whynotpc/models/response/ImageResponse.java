package com.example.whynotpc.models.response;

import com.example.whynotpc.models.img.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ImageResponse extends BasicResponse {
    @JsonProperty
    List<Image> images;

    public ImageResponse(int statusCode, List<Image> images) {
        super(statusCode);
        this.images = images;
    }
    public ImageResponse(int statusCode, Image image) {
        super(statusCode);
        this.images = Collections.singletonList(image);
    }
    public ImageResponse(int statusCode) {
        this(statusCode, Collections.emptyList());
    }
}
