package com.example.laboratornie.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
                       .security { background: #fff3cd; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }
                   </style>
               </head>
               <body>
                   <div class="container">
                       <h1>Система управления отелем!</h1>
                       <p>Это веб-приложение для управления отелем, построенное на Spring Boot с использованием PostgreSQL.</p>
                      
                       <div class="info">
                           <strong>Общие сведения:</strong><br>
                           • Все данные сохраняются в PostgreSQL<br>
                           • Автоматическое создание таблиц при запуске<br>
                           • REST API для управления всеми сущностями
                       </div>
                       
                       <div class="security">
                           <strong>Система безопасности</strong><br>
                           • Используется Basic Authentication<br>
                           • CSRF защита активна<br>
                           • Ролевая модель доступа (USER, MANAGER, ADMIN)<br>
                           • Регистрация новых пользователей через <span class="endpoint">/api/auth/register</span> доступна только ADMIN<br>
                           • Проверка надежности паролей. Используйте надежный пароль (минимум 8 символов, цифры, спецсимволы)
                       </div>
                       
                       <h2>Доступно:</h2>
                       <ul>
                            <li><a href="/api/hotels">GET /api/hotels</a> - Все отели</li>
                            <li><a href="/api/rooms">GET /api/rooms</a> - Все номера</li>
                            <li><a href="/api/guests">GET /api/guests</a> - Все гости</li>
                            <li><a href="/api/bookings">GET /api/bookings</a> - Все брони</li>
                            <li><a href="/api/payments">GET /api/payments</a> - Все платежи</li>
                       </ul>
                                    
                       <h2>Требования к паролю при регистрации:</h2>
                       <ul>
                           <li>Минимум 8 символов</li>
                           <li>Хотя бы одна цифра (0-9)</li>
                           <li>Хотя бы один специальный символ (!@#$%^&*()_+-=[]{}|;:',./<>?)</li>
                           <li>Пример надежного пароля: <span class="endpoint">Password123!@#</span></li>
                       </ul>
                   </div>
               </body>
               </html>
               """;
    }
}