package com.capellax.refresh_token_demo;

import com.capellax.refresh_token_demo.dto.Product;
import com.capellax.refresh_token_demo.model.User;
import com.capellax.refresh_token_demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

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
            int productId
    ) {
        return productList.stream()
                .filter(product -> product.getProductId() == productId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Product (" + productId + ") not found."));
    }

    public String addUser(
            User user
    ) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added to the system!";
    }

}
