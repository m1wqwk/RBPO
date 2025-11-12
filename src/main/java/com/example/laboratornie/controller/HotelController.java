package com.example.laboratornie.controller;

import com.example.laboratornie.model.Hotel;
import com.example.laboratornie.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @PostMapping
    public Hotel createHotel(@RequestBody Hotel hotel) {
        Hotel savedHotel = hotelRepository.save(hotel);
        System.out.println("Создан отель: " + savedHotel);
        return savedHotel;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        System.out.println("Запрошен список всех отелей. Всего: " + hotels.size());
        return hotels;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            System.out.println("Найден отель по ID " + id + ": " + hotel.get());
            return ResponseEntity.ok(hotel.get());
        } else {
            System.out.println("Отель с ID " + id + " не найден.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotelDetails) {
        if (hotelRepository.existsById(id)) {
            hotelDetails.setId(id);
            Hotel updatedHotel = hotelRepository.save(hotelDetails);
            System.out.println("Обновлен отель с ID " + id + ": " + updatedHotel);
            return ResponseEntity.ok(updatedHotel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
            System.out.println("Удален отель с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}