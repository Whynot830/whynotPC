package com.example.whynotpc.utils;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.services.CategoryService;
import com.example.whynotpc.services.ProductService;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        userService.create(new UserDTO("why", "not", "admin",
                "aminfury@mail.ru", "admin", "ADMIN", LocalDateTime.now()));
        categoryService.create(Category.builder().name("storage").build());
        productService.create(List.of(
                new ProductDTO(null, "Kingston A400 SATA SA400S37/480G", 29.99f, "kingston-a400.webp", "storage"),
                new ProductDTO(null, "Samsung 980 M.2 MZ-V8V250BW", 41.99f, "samsung-980-250gb.webp", "storage"),
                new ProductDTO(null, "WD Blue SN570 M.2 WDS100T3B0C", 89.99f, "wd-blue.webp", "storage"),
                new ProductDTO(null, "AMD Radeon SATA R5SL128G", 14.99f, "amd-radeon.webp", "storage"),
                new ProductDTO(null, "Netac SA500 SATA NT01SA500-1T0-S3X", 52.99f, "netac-sa500.webp", "storage"),
                new ProductDTO(null, "GIGABYTE SATA (GP-GSTFS31256GTND)", 24.99f, "gigabyte-256gb.webp", "storage"),
                new ProductDTO(null, "Patriot Memory SATA P210S512G25", 29.99f, "patriot-p210.webp", "storage"),
                new ProductDTO(null, "XPG SX6000 Lite M.2 SX6000", 34.99f, "xpg-sx6000.webp", "storage"),
                new ProductDTO(null, "Samsung 870 QVO SATA MZ-77Q1T0BW", 80.99f, "samsung-870-qvo.webp", "storage"),
                new ProductDTO(null, "Hikvision SATA HS-SSD-C100/120G", 13.99f, "hikvision-120gb.webp", "storage"),
                new ProductDTO(null, "Netac NV3000 M.2 NT01NV3000-500-E4X", 29.99f, "netac-nv3000.webp", "storage"),
                new ProductDTO(null, "HP S700 SATA 6MC15AA#ABB", 59.99f, "hp-s700.webp", "storage"),
                new ProductDTO(null, "ADATA Ultimate SU650 SATA ASU650SS-240GT-R", 19.99f, "adata-su650.webp", "storage"),
                new ProductDTO(null, "Apacer PANTHER 512 ГБ SATA AP512GAS350-1", 30.99f, "apacer-panther.webp", "storage"),
                new ProductDTO(null, "Crucial BX SATA CT240BX500SSD1", 26.99f, "crucial-bx-240gb.webp", "storage"),
                new ProductDTO(null, "KingSpec M.2 NT-256", 17.99f, "kingspec-m2-256gb.webp", "storage")
                ));

    }
}
