package com.swapnild.productService.service;

import com.swapnild.productService.entity.Product;
import com.swapnild.productService.exception.ProductServiceCustomException;
import com.swapnild.productService.model.ProductRequest;
import com.swapnild.productService.model.ProductResponse;
import com.swapnild.productService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Log4j2
@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    public long addProduct(ProductRequest productRequest){
     log.info("Adding Product.....");
        Product product =
                Product.builder()
                        .productName(productRequest.getName())
                        .quantity(productRequest.getQuantity())
                        .price(productRequest.getPrice())
                        .build();
        //we are using builder pattern or we can use beanutils.copyproperties
        productRepository.save(product);
        log.info("Product Added.....");
        return product.getProductId();

    }
    @Override
    public ProductResponse getProductById(long productId){
        log.info("get the product for productID {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceCustomException("Product with given Id not found", "PRODUCT_NOT_FOUND"));

                ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);
        return productResponse;
    }
    @Override
    public void reduceQuantity(long productId, long quantity){

        log.info("Reduce Quantity {} for Id: {}", quantity,productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));


        if(product.getQuantity()< quantity){
            throw new ProductServiceCustomException("Product does not have sufficient quantity", "INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() -quantity);
        productRepository.save(product);
        log.info("Product Quantity Updated Successfully");

    }
}
