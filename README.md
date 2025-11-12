# Лабораторная работа 1–3: Система управления отелем

## Обзор

Веб-приложение, созданное с помощью Spring Boot для автоматизации основных операций в отеле. Система управляет отелями, номерами, гостями, бронированиями и платежами, а также включает в себя ключевую бизнес-логику, например бронирование номеров и обработку платежей.

**Основные сущности:** отель, номер, гость, бронирование, платеж.
**База данных:** PostgreSQL.
## Используемые технологии

*   **Java 21+**
*   **Spring Boot 3.5.6**
*   **Spring Data JPA**
*   **Maven**
*   **Lombok**

## Выполнение

### Настройка среды

1. **Установите PostgreSQL и создайте базу данных:**
```sql
CREATE DATABASE hotel_db
```
2. **Настройте подключение к базе данных в application.properties:**
```sql
spring.datasource.url=jdbc:postgresql://localhost:5432/{base name}(название базы которую мы создали)
spring.datasource.username=postgres (не меняем!!! по умолчанию при регистрации)
spring.datasource.password={password}(вводим пороль)
```
3. **Запуск приложения**
```sql
mvn spring-boot:run
```
Приложение запускается на http://localhost:8080 по умолчанию.

## Endpoints
### Лабораторная 1:
*   `GET /`
    *   Отображает главную страницу с описанием и доступными endpoints
*   `GET /say/{text}`
  *   Отображает пользовательское сообщение


## API Endpoints
### Лабораторная 2:
Управление отелями:

*   `GET /api/hotels`
    *   Получить все отели
*   `GET /api/hotels/{id}`
    *   Получить отель по ID
*   `POST /api/hotels`
    *   Создать новый отель
*   `PUT /api/hotels/{id}`
    *   Обновить отель
*   `DELETE /api/hotels/{id}`
    *   Удалить отель

Управление номерами:

*   `GET /api/rooms`
    *   Получить все номера
*   `GET /api/rooms/{id}`
    *   Получить номер по ID
*   `GET /api/rooms/hotel/{hotelId}`
    *   Получить номера по отелю
*   `POST /api/rooms`
    *   Создать новый номер
*   `PUT /api/rooms/{id}`
    *   Обновить номер
*   `DELETE /api/rooms/{id}`
    *   Удалить номер

Управление гостями:

*   `GET /api/guests`
    *   Получить всех гостей
*   `GET /api/guests/{id}`
    *   Получить гостя по ID
*   `POST /api/guests`
    *   Создать нового гостя
*   `PUT /api/guests/{id}`
    *   Обновить гостя
*   `DELETE /api/guests/{id}`
    *   Удалить гостя

Управление бронями:

*   `GET /api/bookings`
    *   Получить все брони
*   `GET /api/bookings/{id}`
    *   Получить бронь по ID
*   `GET /api/bookings/guest/{guestId}`
    *   Получить брони по гостю
*   `POST /api/bookings`
    *   Создать новую бронь (с проверкой пересечений дат)
*   `PUT /api/bookings/{id}`
    *   Обновить бронь
*   `DELETE /api/bookings/{id}`
    *   Удалить бронь
*   `PATCH /api/bookings/{id}/cancel`
    *   Отменить бронь

Управление платежами:

*   `GET /api/payments`
    *   Получить все платежи
*   `GET /api/payments/{id}`
    *   Получить отель по ID
*   `GET /api/payments/booking/{bookingId}`
    *   Получить платежи по брони
*   `POST /api/payments`
    *   Создать новый платеж
*   `PUT /api/payments/{id}`
    *   Обновить платеж
*   `DELETE /api/payments/{id}`
    *   Удалить платеж
*   `PATCH /api/payments/{id}/complete`
    *   Отметить платеж как завершенный

### Лабораторная 3:
Быстрые команды для проверки:
1. **Проверить главную страницу**
```sql
GET http://localhost:8080/
```
2. **Создать гостя**
```sql
POST http://localhost:8080/api/guests
Content-Type: application/json

{
  "firstName": "Иван",
  "lastName": "Иванов", 
  "email": "IvanovIvanI@mail.ru",
  "phone": "+7-9XX-XXX-XX-XX",
  "passportNumber": "XXXX 123456"
}
```
3. **Найти свободные номера**
```sql
GET http://localhost:8080/api/rooms/available
```
4. **Забронировать номер**
```sql
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "checkInDate": "2025-12-30",
  "checkOutDate": "2026-01-6",
  "guest": {"id": 1},
  "room": {"id": 1}
}
```
5. **Отменить бронь**
```sql
PATCH http://localhost:8080/api/bookings/1/cancel
```
6. **Статистика платежей**
```sql
GET http://localhost:8080/api/payments/stats/summary
```

