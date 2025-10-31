package com.example.laboratornie.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Hotel {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Integer stars;
}