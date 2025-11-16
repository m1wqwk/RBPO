package com.example.laboratornie.DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private Double amount;
    private String paymentMethod;
    private String status;
    private Long bookingId;
}
