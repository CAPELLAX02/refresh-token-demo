package com.capellax.refresh_token_demo.service;

import com.capellax.refresh_token_demo.dto.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProductService {

    List<Product> productList = null;

    @PostConstruct
    public void loadDummyProducts() {
        productList = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> Product.builder()
                        .productId((long) i)
                        .productName("product " + i)
                        .quantity(new Random().nextInt())
                        .price(new Random().nextInt(5000))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return productList;
    }

    public Product getProductById(
            Long productId
    ) {
        return productList.stream()
                .filter(product -> Objects.equals(product.getProductId(), productId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Product (" + productId + ") not found."));
    }

}
