package com.swapnild.productService.service;

import com.swapnild.productService.model.ProductRequest;
import com.swapnild.productService.model.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
