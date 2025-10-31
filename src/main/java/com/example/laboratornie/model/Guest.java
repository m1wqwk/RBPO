package com.example.laboratornie.model;

import lombok.Data;

@Data
public class Guest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passportNumber;
}