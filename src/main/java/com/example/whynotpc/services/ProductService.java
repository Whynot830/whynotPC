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

/**
 * Service class responsible for managing products.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final OrderItemRepo orderItemRepo;
    private final ImageService imageService;

    /**
     * Retrieves a category by its name.
     *
     * @param name The name of the category to retrieve
     * @return retrieved category
     * @throws EntityNotFoundException if no category with the given name exists
     */
    private Category getCategory(String name) {
        return categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Saves a new product based on the provided DTO and image file.
     *
     * @param productDTO The DTO containing product information
     * @param file       The image file associated with the product (optional)
     * @return saved product
     * @throws IllegalArgumentException if some properties in the productDTO are null or blank, or if the price is negative
     */
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

    /**
     * Saves a new product based on the provided DTO and image file.
     *
     * @param productDTO The DTO containing product information
     * @param file       The image file associated with the product (optional)
     * @return ProductResponse containing the saved product
     * @throws IllegalArgumentException if some properties in the productDTO are null or blank, or if the price is negative
     */
    public ProductResponse create(ProductDTO productDTO, MultipartFile file) {
        var product = save(productDTO, file);
        return ok(product);
    }

    /**
     * Saves a list of products based on the provided DTOs.
     *
     * @param products list of DTOs containing product information
     * @return ProductResponse containing the saved products
     */
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

    /**
     * Creates a PageRequest object for pagination and sorting based on the provided parameters.
     *
     * @param page  The page number (starts from 0). If null, defaults to the first page (0).
     * @param sort  The field to sort by. If null, no sorting will be applied.
     * @param order The sort order, either "asc" or "desc". If null, defaults to ascending order.
     * @return PageRequest object configured with the specified pagination and sorting settings.
     */
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

    /**
     * Retrieves products based on the provided parameters.
     *
     * @param categoryName The name of the category to filter products by (optional)
     * @param page         The page number for pagination (optional)
     * @param sort         The field to sort by (optional)
     * @param order        The sorting order (optional)
     * @return BasicResponse containing the retrieved products
     */
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

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve
     * @return ProductResponse containing the retrieved product
     * @throws EntityNotFoundException if no product with the given ID exists
     */
    public ProductResponse read(Long id) {
        return productRepo.findById(id)
                .map(ProductResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Updates a product based on the provided ID, DTO, and image file.
     *
     * @param id        The ID of the product to update
     * @param newProduct The DTO containing the updated product information (optional)
     * @param file      The new image file associated with the product (optional)
     * @return ProductResponse containing the updated product
     * @throws EntityNotFoundException if no product with the given ID exists
     * @throws IllegalArgumentException if the new product category is not found, or if the price is negative
     */
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

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete
     * @return BasicResponse indicating the success of the operation
     * @throws EntityNotFoundException if no product with the given ID exists
     */
    public BasicResponse delete(Long id) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepo.delete(product);
        return noContent();
    }

    /**
     * Deletes all products.
     *
     * @return BasicResponse indicating the success of the operation
     */
    public BasicResponse deleteAll() {
        productRepo.deleteAll();
        return noContent();
    }
}
