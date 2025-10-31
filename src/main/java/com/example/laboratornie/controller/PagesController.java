package com.example.laboratornie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PagesController {

    @GetMapping("/")
    public String index() {
        return """
               <html>
               <head><title>Система управления отелем!</title></head>
               <body>
                   <h1>Добро пожаловать в систему управления отелем</h1>
                   <p>Это веб-приложение для управления отелем, построенное на Spring Boot.</p>
                   
                   <h2>Доступно:</h2>
                   <ul>
                       <li><a href="/api/hotels">GET /api/hotels</a> - Все отели</li>
                       <li><a href="/api/rooms">GET /api/rooms</a> - Все номера</li>
                       <li><a href="/api/guests">GET /api/guests</a> - Все гости</li>
                       <li><a href="/api/bookings">GET /api/bookings</a> - Все брони</li>
                       <li><a href="/api/payments">GET /api/payments</a> - Все платежи</li>
                   </ul>
               </body>
               </html>
               """;
    }

    @GetMapping("/say/{text}")
    public String saySomething(@PathVariable String text) {
        return "<h1>Вы сказали:</h1><p style='font-size: 24px; color: #a9f5a5;'>" + text + "</p>";
    }
}