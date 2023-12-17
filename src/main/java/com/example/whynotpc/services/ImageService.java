package com.example.whynotpc.services;

import com.example.whynotpc.models.img.Image;
import com.example.whynotpc.models.response.ImageResponse;
import com.example.whynotpc.persistence.img.ImageRepo;
import com.example.whynotpc.utils.ImageUtils;
import com.example.whynotpc.utils.JPACallHandler.Result;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import static com.example.whynotpc.utils.JPACallHandler.handleCall;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepo imageRepo;

    private Result<Image> save(MultipartFile file) {
        return handleCall(() -> {
            try {
                return imageRepo.save(Image.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes()))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
    }

    public ImageResponse create(MultipartFile file) {
        var response = save(file);
        return switch (response.statusCode()) {
            case 200 -> new ImageResponse(201, response.result());
            case 409 -> new ImageResponse(409);
            default -> new ImageResponse(500);
        };
    }

    @Transactional
    public ImageResponse create(MultipartFile[] files) {
        List<Image> savedImages = new ArrayList<>();
        Result<Image> response;
        for (var file : files) {
            if (imageRepo.existsByName(file.getOriginalFilename())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ImageResponse(409);
            }
            response = save(file);
            if (response.result() == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ImageResponse(response.statusCode());
            }
            savedImages.add(response.result());
        }
        return new ImageResponse(201, savedImages);
    }

    public byte[] read(String filename) throws DataFormatException, IOException {
        var imageData = imageRepo.findByName(filename).orElse(null);
        if (imageData != null)
            return ImageUtils.decompressImage(imageData.getImageData());
        return null;
    }

    public ImageResponse readAll() {
        return new ImageResponse(200, imageRepo.findAll());
    }

    public ImageResponse update(MultipartFile file, String filename) {
        var response = handleCall(() -> {
            var image = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
            try {
                return imageRepo.save(Image.builder()
                        .id(image.getId())
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes()))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
        return switch (response.statusCode()) {
            case 200 -> new ImageResponse(200, response.result());
            case 404 -> new ImageResponse(404);
            case 409 -> new ImageResponse(409);
            default -> new ImageResponse(500);
        };
    }

    public ImageResponse deleteAll() {
        imageRepo.deleteAll();
        return new ImageResponse(204);
    }

    public ImageResponse delete(String name) {
        var response = handleCall(() -> {
            var image = imageRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
            imageRepo.delete(image);
            return true;
        });
        return switch (response.statusCode()) {
            case 200 -> new ImageResponse(204);
            case 404 -> new ImageResponse(404);
            default -> new ImageResponse(500);
        };
    }
}
