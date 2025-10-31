package com.example.laboratornie.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Booking {
    private Long id;
    private Long guestId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private Double totalPrice;
}