package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.DataFormatException;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<? extends BasicResponse> create(@RequestParam MultipartFile file) {
        return getResponse(() -> imageService.create(file));
    }

    @PostMapping("/all")
    public ResponseEntity<? extends BasicResponse> create(@RequestParam MultipartFile[] files) {
        return getResponse(() -> imageService.create(files));
    }

    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(imageService::readAll);
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> read(@PathVariable String name) throws DataFormatException, IOException {
        byte[] imageData = imageService.read(name);
        return ResponseEntity.status(OK).contentType(IMAGE_PNG).body(imageData);
    }

    @PatchMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> update(@RequestParam MultipartFile file, @PathVariable String name) {
        return getResponse(() -> imageService.update(file, name));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable String name) {
        return getResponse(() -> imageService.delete(name));
    }

    @DeleteMapping
    public ResponseEntity<? extends BasicResponse> deleteAll() {
        return getResponse(imageService::deleteAll);
    }
}
