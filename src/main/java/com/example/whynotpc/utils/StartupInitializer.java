package com.example.whynotpc.utils;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.services.CategoryService;
import com.example.whynotpc.services.ProductService;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        try {
            userService.create(new UserDTO("why", "not", "admin",
                    "aminfury@mail.ru", "admin", "ADMIN", LocalDateTime.now()));
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            categoryService.create(Category.builder().name("storage").build());
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            productService.create(List.of(
                    new ProductDTO(null, "Kingston A400 SATA SA400S37/480G", BigDecimal.valueOf(22.99), "kingston-a400.webp", "storage"),
                    new ProductDTO(null, "Samsung 980 M.2 MZ-V8V250BW", BigDecimal.valueOf(41.99), "samsung-980-250gb.webp", "storage"),
                    new ProductDTO(null, "WD Blue SN570 M.2 WDS100T3B0C", BigDecimal.valueOf(89.99), "wd-blue.webp", "storage"),
                    new ProductDTO(null, "AMD Radeon SATA R5SL128G", BigDecimal.valueOf(14.99), "amd-radeon.webp", "storage"),
                    new ProductDTO(null, "Netac SA500 SATA NT01SA500-1T0-S3X", BigDecimal.valueOf(52.99), "netac-sa500.webp", "storage"),
                    new ProductDTO(null, "GIGABYTE SATA (GP-GSTFS31256GTND)", BigDecimal.valueOf(24.99), "gigabyte-256gb.webp", "storage"),
                    new ProductDTO(null, "Patriot Memory SATA P210S512G25", BigDecimal.valueOf(29.99), "patriot-p210.webp", "storage"),
                    new ProductDTO(null, "XPG SX6000 Lite M.2 SX6000", BigDecimal.valueOf(34.99), "xpg-sx6000.webp", "storage"),
                    new ProductDTO(null, "Samsung 870 QVO SATA MZ-77Q1T0BW", BigDecimal.valueOf(80.99), "samsung-870-qvo.webp", "storage"),
                    new ProductDTO(null, "Hikvision SATA HS-SSD-C100/120G", BigDecimal.valueOf(13.99), "hikvision-120gb.webp", "storage"),
                    new ProductDTO(null, "Netac NV3000 M.2 NT01NV3000-500-E4X", BigDecimal.valueOf(29.99), "netac-nv3000.webp", "storage"),
                    new ProductDTO(null, "HP S700 SATA 6MC15AA#ABB", BigDecimal.valueOf(59.99), "hp-s700.webp", "storage"),
                    new ProductDTO(null, "ADATA Ultimate SU650 SATA ASU650SS-240GT-R", BigDecimal.valueOf(19.99), "adata-su650.webp", "storage"),
                    new ProductDTO(null, "Apacer PANTHER 512 ГБ SATA AP512GAS350-1", BigDecimal.valueOf(30.99), "apacer-panther.webp", "storage"),
                    new ProductDTO(null, "Crucial BX SATA CT240BX500SSD1", BigDecimal.valueOf(26.99), "crucial-bx-240gb.webp", "storage"),
                    new ProductDTO(null, "KingSpec M.2 NT-256", BigDecimal.valueOf(17.99), "kingspec-m2-256gb.webp", "storage")
            ));
        } catch (DataIntegrityViolationException ignored) {
        }


    }
}
