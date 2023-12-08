package com.example.whynotpc.controllers;

import com.example.whynotpc.models.img.Image;
import com.example.whynotpc.models.response.ImageResponse;
import com.example.whynotpc.services.ImageService;
import com.example.whynotpc.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageResponse> create(@RequestParam MultipartFile file) {
        return handleServiceCall(() -> imageService.create(file));
    }
    @PostMapping("/all")
    public ResponseEntity<ImageResponse> create(@RequestParam MultipartFile[] files) {
        return handleServiceCall(() -> imageService.create(files));
    }
    @GetMapping("/{name}")
    public ResponseEntity<?> read(@PathVariable String name) throws DataFormatException, IOException {
        byte[] imageData = imageService.read(name);
        return imageData != null ? ResponseEntity.status(OK).contentType(MediaType.IMAGE_PNG).body(imageData)
                : ResponseEntity.notFound().build();
    }
    @DeleteMapping
    public ResponseEntity<ImageResponse> delete() {
        return handleServiceCall(imageService::delete);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ImageResponse> delete(@PathVariable String name) {
        return handleServiceCall(() -> imageService.delete(name));
    }
}
