package com.example.laboratornie.DTO;

import lombok.Data;

@Data
public class RoomRequest {
    private String number;
    private String type;
    private Double pricePerNight;
    private Integer capacity;
    private Boolean available;
    private Long hotelId;
}
