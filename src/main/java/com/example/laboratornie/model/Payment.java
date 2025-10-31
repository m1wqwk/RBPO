package com.example.laboratornie.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private Long bookingId;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod; // CASH, CARD, TRANSFER
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
}