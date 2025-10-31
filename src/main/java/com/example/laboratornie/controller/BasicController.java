package com.example.laboratornie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
public class BasicController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("/time")
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Time: " + now.format(formatter);
    }

    @GetMapping("/location")
    public String getCurrentLocation() {
        Locale defaultLocale = Locale.getDefault();
        return "Location: " + defaultLocale.getDisplayCountry() +
                " (" + defaultLocale.getCountry() + ")";
    }

    @GetMapping("/")
    public String home() {
        return "Главная страничка типо. Доступно: /hello, /time, /location";
    }
}