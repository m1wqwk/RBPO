package com.example.laboratornie.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private String guestEmail;
    private String guestFirstName;
    private String guestLastName;
    private String guestPhone;
    private String guestPassport;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String paymentMethod;
}