package com.swapnild.PaymentService.service;

import com.swapnild.PaymentService.model.PaymentRequest;
import com.swapnild.PaymentService.model.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
