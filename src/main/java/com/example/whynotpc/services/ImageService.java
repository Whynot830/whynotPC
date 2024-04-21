package com.example.whynotpc.services;

import com.example.whynotpc.models.img.Image;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.ImageResponse;
import com.example.whynotpc.persistence.img.ImageRepo;
import com.example.whynotpc.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

/**
 * Service class responsible for handling image-related operations.
 */
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepo imageRepo;

    /**
     * Saves a single image file to the database.
     *
     * @param file The multipart file containing the image data
     * @return saved image
     */
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

    /**
     * Creates and saves a single image from the provided multipart file.
     *
     * @param file The multipart file containing the image data
     * @return response containing the created image
     */
    public ImageResponse create(MultipartFile file) {
        var image = save(file);
        return created(image);
    }

    /**
     * Creates and saves multiple images from the provided array of multipart files.
     *
     * @param files The array of multipart files containing the image data
     * @return response containing the created images
     */
    @Transactional
    public ImageResponse create(MultipartFile[] files) {
        List<Image> savedImages = new ArrayList<>();
        Image savedImage;
        for (var file : files) {
            savedImage = save(file);
            savedImages.add(savedImage);
        }
        return created(savedImages);
    }

    /**
     * Retrieves an image by its filename.
     *
     * @param filename The name of the image file
     * @return retrieved image, or null if not found
     */
    public Image read(String filename) {
        return imageRepo.findByName(filename).orElse(null);
    }

    /**
     * Retrieves the image data (as a byte array) for the specified filename.
     *
     * @param filename The name of the image file
     * @return image data as a byte array
     * @throws RuntimeException If an error occurs while retrieving the image data
     */
    public byte[] getImage(String filename) {
        try {
            var imageData = imageRepo.findByName(filename).orElseThrow(EntityNotFoundException::new);
            return ImageUtils.decompressImage(imageData.getImageData());
        } catch (DataFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all images from the database.
     *
     * @return response containing all images
     */
    public ImageResponse readAll() {
        return ok(imageRepo.findAll());
    }

    /**
     * Updates an existing image with new data from the provided multipart file.
     *
     * @param file     The multipart file containing the updated image data
     * @param filename The name of the image file to update
     * @return response containing the updated image
     */
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

    /**
     * Deletes all images from the database.
     *
     * @return response indicating success
     */
    public BasicResponse deleteAll() {
        imageRepo.deleteAll();
        return noContent();
    }

    /**
     * Deletes the image with the specified filename from the database.
     *
     * @param name The name of the image file to delete
     * @return response indicating success
     * @throws EntityNotFoundException If the specified image is not found in the database
     */
    public BasicResponse delete(String name) {
        var image = imageRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        imageRepo.delete(image);

        return noContent();
    }
}
