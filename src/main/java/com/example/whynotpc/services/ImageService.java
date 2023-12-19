package com.example.whynotpc.services;

import com.example.whynotpc.models.img.Image;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.ImageResponse;
import com.example.whynotpc.persistence.img.ImageRepo;
import com.example.whynotpc.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;

import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.ImageResponse.created;
import static com.example.whynotpc.models.response.ImageResponse.ok;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepo imageRepo;

    private Image save(MultipartFile file) {
        try {
            return imageRepo.save(Image.builder()
                    .name(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'))
                    .type(file.getContentType())
                    .imageData(ImageUtils.compressImage(file.getBytes()))
                    .build());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public ImageResponse create(MultipartFile file) {
        var image = save(file);
        return created(image);
    }

    @Transactional
    public ImageResponse create(MultipartFile[] files) {
        List<Image> savedImages = new ArrayList<>();
        Image image;
        for (var file : files) {
            if (imageRepo.existsByName(file.getOriginalFilename()))
                throw new DataIntegrityViolationException("File with name '" + file.getOriginalFilename() + "' already exists");

            image = save(file);
            savedImages.add(image);
        }
        return created(savedImages);
    }

    public byte[] read(String filename) throws DataFormatException, IOException {
        var imageData = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
        return ImageUtils.decompressImage(imageData.getImageData());
    }

    public ImageResponse readAll() {
        return ok(imageRepo.findAll());
    }

    public ImageResponse update(MultipartFile file, String filename) {
        var oldImage = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
        try {
            var image = imageRepo.save(Image.builder()
                    .id(oldImage.getId())
                    .name(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'))
                    .type(file.getContentType())
                    .imageData(ImageUtils.compressImage(file.getBytes()))
                    .build());
            return ok(image);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public BasicResponse deleteAll() {
        imageRepo.deleteAll();
        return noContent();
    }

    public BasicResponse delete(String name) {
        var image = imageRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        imageRepo.delete(image);

        return noContent();
    }
}
