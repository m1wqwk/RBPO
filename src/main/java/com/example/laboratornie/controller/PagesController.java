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
               <head>
                   <title>Система управления отелем!</title>
                   <style>
                       body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                       .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                       h1 { color: #2c3e50; }
                       h2 { color: #34495e; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                       ul { list-style-type: none; padding: 0; }
                       li { margin: 10px 0; padding: 10px; background: #ecf0f1; border-radius: 5px; }
                       a { text-decoration: none; color: #2980b9; font-weight: bold; }
                       a:hover { color: #3498db; }
                       .info { background: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin: 20px 0; }
                   </style>
               </head>
               <body>
                   <div class="container">
                       <h1>Добро пожаловать в систему управления отелем!</h1>
                       <p>Это веб-приложение для управления отелем, построенное на Spring Boot с использованием PostgreSQL.</p>
                       
                       <div class="info">
                           Все данные теперь сохраняются в PostgreSQL и сохраняются между перезапусками приложения.
                       </div>
                       
                       <h2>Основные API:</h2>
                       <ul>
                           <li><a href="/api/hotels" target="_blank">GET /api/hotels</a> - Все отели</li>
                           <li><a href="/api/rooms" target="_blank">GET /api/rooms</a> - Все номера</li>
                           <li><a href="/api/guests" target="_blank">GET /api/guests</a> - Все гости</li>
                           <li><a href="/api/bookings" target="_blank">GET /api/bookings</a> - Все брони</li>
                           <li><a href="/api/payments" target="_blank">GET /api/payments</a> - Все платежи</li>
                       </ul>
                       
                       <h2>Дополнительные endpoints:</h2>
                       <ul>
                           <li><strong>Гости:</strong> GET /api/guests/email/{email}, GET /api/guests/passport/{passport}</li>
                           <li><strong>Номера:</strong> GET /api/rooms/available, GET /api/rooms/hotel/{hotelId}</li>
                           <li><strong>Платежи:</strong> GET /api/payments/status/{status}, GET /api/payments/stats/summary</li>
                           <li><strong>Бронирования:</strong> GET /api/bookings/guest/{guestId}</li>
                       </ul>
                       
                       <h2>Методы HTTP:</h2>
                       <p>Для каждого endpoint доступны стандартные операции CRUD:</p>
                       <ul>
                           <li><strong>POST</strong> - создание новой записи</li>
                           <li><strong>GET</strong> - получение данных</li>
                           <li><strong>PUT</strong> - полное обновление</li>
                           <li><strong>PATCH</strong> - выборочное обновление</li>
                           <li><strong>DELETE</strong> - удаление</li>
                       </ul>
                   </div>
               </body>
               </html>
               """;
    }

    @GetMapping("/say/{text}")
    public String saySomething(@PathVariable String text) {
        return """
               <html>
               <head>
                   <style>
                       body { font-family: Arial, sans-serif; margin: 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
                       .message { background: rgba(255,255,255,0.1); padding: 30px; border-radius: 15px; backdrop-filter: blur(10px); text-align: center; }
                       h1 { font-size: 24px; margin-bottom: 20px; }
                       p { font-size: 32px; font-weight: bold; color: #a9f5a5; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }
                   </style>
               </head>
               <body>
               </body>
               </html>
               """.formatted(text);
    }
}