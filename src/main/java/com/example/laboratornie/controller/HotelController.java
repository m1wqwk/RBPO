package com.example.laboratornie.controller;

import com.example.laboratornie.model.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final Map<Long, Hotel> hotels = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public HotelController() {
        long initialId = counter.incrementAndGet();
        Hotel initialHotel = new Hotel();
        initialHotel.setId(initialId);
        initialHotel.setName("Отель Элеон");
        initialHotel.setAddress("ул. Центральная, 1");
        initialHotel.setPhone("+7-495-123-45-67");
        initialHotel.setStars(5);
        hotels.put(initialId, initialHotel);
    }

    @PostMapping
    public Hotel createHotel(@RequestBody Hotel hotel) {
        long newId = counter.incrementAndGet();
        hotel.setId(newId);
        hotels.put(newId, hotel);
        System.out.println("Создан отель: " + hotel);
        return hotel;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        System.out.println("Запрошен список всех отелей. Всего: " + hotels.size());
        return new ArrayList<>(hotels.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        Hotel hotel = hotels.get(id);
        if (hotel != null) {
            System.out.println("Найден отель по ID " + id + ": " + hotel);
            return ResponseEntity.ok(hotel);
        } else {
            System.out.println("Отель с ID " + id + " не найден.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotelDetails) {
        if (hotels.containsKey(id)) {
            hotelDetails.setId(id);
            hotels.put(id, hotelDetails);
            System.out.println("Обновлен отель с ID " + id + ": " + hotelDetails);
            return ResponseEntity.ok(hotelDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        if (hotels.remove(id) != null) {
            System.out.println("Удален отель с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
