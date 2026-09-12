package com.swapnild.OrderService.service;

import com.swapnild.OrderService.model.OrderRequest;
import com.swapnild.OrderService.model.OrderResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
