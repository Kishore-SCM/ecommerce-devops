package com.ecommerce.product.service;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j @Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final S3Client           s3Client;
    private final MeterRegistry      meterRegistry;
    private static final String BUCKET = "ecommerce-assets";
    private static final String CACHE  = "products";

    @Cacheable(value = CACHE, key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getById(String id) {
        return productRepo.findById(id).map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(Product.Category category,
            BigDecimal minPrice, BigDecimal maxPrice,
            String search, int page, int size, String sort) {
        Sort s = switch (sort) {
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating"     -> Sort.by("rating").descending();
            default           -> Sort.by("createdAt").descending();
        };
        return productRepo.searchProducts(category, minPrice, maxPrice, search,
            PageRequest.of(page, size, s)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getFeatured() {
        return productRepo.findByFeaturedTrueAndActiveTrue().stream()
            .map(this::toResponse).toList();
    }

    public ProductResponse create(CreateProductRequest req) {
        Product p = Product.builder()
            .name(req.name()).description(req.description())
            .price(req.price()).stock(req.stock()).sku(req.sku())
            .category(req.category()).featured(req.featured())
            .active(true).rating(0.0).reviewCount(0).build();
        Product saved = productRepo.save(p);
        Counter.builder("product.created").tag("category", saved.getCategory().name())
            .register(meterRegistry).increment();
        return toResponse(saved);
    }

    @CacheEvict(value = CACHE, key = "#id")
    public ProductResponse update(String id, UpdateProductRequest req) {
        Product p = productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        if (req.name()     != null) p.setName(req.name());
        if (req.price()    != null) p.setPrice(req.price());
        if (req.stock()    != null) p.setStock(req.stock());
        if (req.featured() != null) p.setFeatured(req.featured());
        return toResponse(productRepo.save(p));
    }

    @CacheEvict(value = CACHE, key = "#id")
    public void delete(String id) {
        Product p = productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        p.setActive(false);
        productRepo.save(p);
    }

    public String uploadImage(String productId, MultipartFile file) throws IOException {
        String key = "products/" + productId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder().bucket(BUCKET).key(key)
            .contentType(file.getContentType()).build(),
            software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        String url = "https://" + BUCKET + ".s3.amazonaws.com/" + key;
        productRepo.findById(productId).ifPresent(p -> { p.getImageUrls().add(url); productRepo.save(p); });
        return url;
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
            p.getPrice(), p.getStock(), p.getSku(), p.getCategory(),
            p.getImageUrls(), p.getRating(), p.getReviewCount(),
            p.getFeatured(), p.getCreatedAt());
    }
}
