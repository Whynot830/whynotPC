package com.example.whynotpc.persistence.img;

import com.example.whynotpc.models.img.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepo extends JpaRepository<Image, Integer> {
    Optional<Image> findByName(String name);

    boolean existsByName(String name);
}
