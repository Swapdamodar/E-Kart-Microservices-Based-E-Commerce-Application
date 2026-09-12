package com.swapnild.OrderService.external.response;

import com.swapnild.PaymentService.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private long paymentId;
    private long amount;
    private String status;
    private PaymentMode paymentMode;
    private Instant paymentDate;
    private long orderId;

}
