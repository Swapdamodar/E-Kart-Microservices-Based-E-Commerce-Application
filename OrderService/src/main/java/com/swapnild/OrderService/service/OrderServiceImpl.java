package com.swapnild.OrderService.service;

import com.swapnild.OrderService.entity.Order;
import com.swapnild.OrderService.exception.CustomException;
import com.swapnild.OrderService.external.client.PaymentService;
import com.swapnild.OrderService.external.client.ProductService;
import com.swapnild.OrderService.model.OrderRequest;
import com.swapnild.OrderService.model.OrderResponse;
import com.swapnild.OrderService.repository.OrderRepository;
import com.swapnild.PaymentService.model.PaymentRequest;
import com.swapnild.PaymentService.model.PaymentResponse;
import com.swapnild.productService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    public long placeOrder(OrderRequest orderRequest){

        //Order service - save the data with status order created
        //Product Service - Block the product(reduce the quantity)
        //Payment Service - payments -> success/ cancelled status
        log.info("Placing Order Request : ", orderRequest);


        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating Order with status Created");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .productId(orderRequest.getProductId())
                .build();
        order = orderRepository.save(order);
        log.info("Calling payment service to complete the payment");

        PaymentRequest paymentRequest
                = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(com.swapnild.PaymentService.model.PaymentMode.valueOf(orderRequest.getPaymentMode().name()))
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the Oder status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order Placed with order ID {}", order.getId());

        return order.getId();
    }
    public OrderResponse getOrderDetails(long orderId){
        log.info("Get Order Details for orderId {}",orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException("Order not found for the order Id:" + orderId,
                        "NOT_FOUND",
                        404));



        log.info("Invoking product service to fetch the Product for the id {}",order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject("http://product-service/product/" +order.getProductId(),ProductResponse.class);
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();


        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://Payment-Service/payment/order/" + order.getId(),
                PaymentResponse.class
        );
        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(
                        com.swapnild.OrderService.model.PaymentMode.valueOf(
                                paymentResponse.getPaymentMode().name()
                        )
                )
                .build();



        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();



                return orderResponse;
    }
}
