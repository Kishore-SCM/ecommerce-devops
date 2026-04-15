package com.ecommerce.product.controller;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Search and filter products")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) Product.Category category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "20")     int size,
            @RequestParam(defaultValue = "newest") String sort) {
        return ResponseEntity.ok(productService.search(category, minPrice, maxPrice, search, page, size, sort));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> featured() {
        return ResponseEntity.ok(productService.getFeatured());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable String id, @Valid @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(productService.uploadImage(id, file));
    }
}
