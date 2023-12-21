package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.product.Product;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.ProductPageResponse;
import com.example.whynotpc.models.response.ProductResponse;
import com.example.whynotpc.persistence.orders.OrderItemRepo;
import com.example.whynotpc.persistence.products.CategoryRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.ProductResponse.created;
import static com.example.whynotpc.models.response.ProductResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.valueOf;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final OrderItemRepo orderItemRepo;
    private final ImageService imageService;

    private Category getCategory(String name) {
        return categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
    }

    private Product save(ProductDTO productDTO, MultipartFile file) {
        if (isNullOrBlank(productDTO.title()) || isNullOrBlank(productDTO.category()) || productDTO.price() == null)
            throw new IllegalArgumentException("Some properties are null or blank");

        if (productDTO.price().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be a non-negative value");

        var category = categoryRepo
                .findByName(productDTO.category())
                .orElseThrow(() -> new IllegalArgumentException("Category with given name not found"));

        var product = Product.builder()
                .title(productDTO.title())
                .price(productDTO.price())
                .category(category)
                .build();

        if (file != null) {
            imageService.create(file);
            product.setImgName(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'));
        }
        return productRepo.save(product);
    }

    public ProductResponse create(ProductDTO productDTO, MultipartFile file) {
        var product = save(productDTO, file);
        return ok(product);
    }

    @Transactional
    public ProductResponse create(List<ProductDTO> products) {
        List<Product> savedProducts = new ArrayList<>();
        Product savedProduct;
        for (var product : products) {
            savedProduct = save(product, null);
            savedProducts.add(savedProduct);
        }
        return created(savedProducts);
    }

    private PageRequest createPageRequest(Integer page, String sort, String order) {
        if (page == null)
            page = 0;
        Sort sortingStrategy;
        if (sort == null)
            sortingStrategy = Sort.unsorted();
        else {
            Sort.Direction direction;
            try {
                direction = (order == null) ? ASC : valueOf(order.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                direction = ASC;
            }
            sortingStrategy = Sort.by(direction, sort);
        }
        return PageRequest.of(page, 12, sortingStrategy);
    }
    public BasicResponse read(String categoryName, Integer page, String sort, String order) {
        List<Product> products;
        Page<Product> productsPage;
        if (page == null && sort == null) {
            if (categoryName == null) { // All products
                products = productRepo.findAll();
                return ok(products);
            }
            // All products by category
            var category = getCategory(categoryName);
            products = productRepo.findAllByCategory(category);
            return ProductPageResponse.ok(products);
        }
        // Params included
        var pageRequest = createPageRequest(page, sort, order);
        Category category;
        try {
            category = getCategory(categoryName);
            productsPage = productRepo.findPageByCategory(category, pageRequest);
        } catch (EntityNotFoundException ignored) {
            productsPage = productRepo.findAll(pageRequest);
        }
        return ProductPageResponse.ok(productsPage);
    }

    public ProductResponse read(Long id) {
        return productRepo.findById(id)
                .map(ProductResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    public ProductResponse update(Long id, ProductDTO newProduct, MultipartFile file) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);

        if (newProduct != null) {
            var category = categoryRepo.findByName(newProduct.category()).orElse(null);
            if (newProduct.category() != null && category == null)
                throw new IllegalArgumentException("Category with given name not found");
            if (newProduct.price() != null && newProduct.price().compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price must be a non-negative value");

            if (!isNullOrBlank(newProduct.title())) product.setTitle(newProduct.title());
            if (newProduct.price() != null) product.setPrice(newProduct.price());
            if (!isNullOrBlank(newProduct.category())) product.setCategory(category);
        }

        if (file != null) {
            var presentImage = imageService.read(product.getImgName());

            if (presentImage == null) imageService.create(file);
            else imageService.update(file, presentImage.getName());

            product.setImgName(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'));
        }
        var items = product.getOrderItems();
        orderItemRepo.saveAll(items);
        product = productRepo.save(product);

        return ok(product);
    }

    public BasicResponse delete(Long id) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepo.delete(product);
        return noContent();
    }


    public BasicResponse deleteAll() {
        productRepo.deleteAll();
        return noContent();
    }
}
