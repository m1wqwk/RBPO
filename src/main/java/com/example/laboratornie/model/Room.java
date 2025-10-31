package com.example.laboratornie.model;

import lombok.Data;

@Data
public class Room {
    private Long id;
    private Long hotelId;
    private String number;
    private String type;
    private Double pricePerNight;
    private Integer capacity;
    private Boolean available;
}