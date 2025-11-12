package com.example.laboratornie.controller;

import com.example.laboratornie.model.Room;
import com.example.laboratornie.repository.RoomRepository;
import com.example.laboratornie.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.laboratornie.DTO.AvailabilityRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomController(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        // Проверка существования отеля
        if (!hotelRepository.existsById(room.getHotel().getId())) {
            return ResponseEntity.badRequest().body("Отель с ID: " + room.getHotel().getId() + " отсутствует в базе.");
        }

        // Проверка уникальности номера комнаты
        List<Room> existingRooms = roomRepository.findByHotelId(room.getHotel().getId());
        boolean roomNumberExists = existingRooms.stream()
                .anyMatch(r -> r.getNumber().equals(room.getNumber()));

        if (roomNumberExists) {
            return ResponseEntity.badRequest()
                    .body("ОШИБКА! Номер '" + room.getNumber() + "' присутствует в базе.");
        }

        Room savedRoom = roomRepository.save(room);
        System.out.println("Создан номер: " + savedRoom);
        return ResponseEntity.ok(savedRoom);
    }

    @GetMapping
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        System.out.println("Запрошен список всех номеров. Всего: " + rooms.size());
        return rooms;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            System.out.println("Найден номер по ID: " + id + ": " + room.get());
            return ResponseEntity.ok(room.get());
        } else {
            System.out.println("Номер с ID: " + id + " отсутствует в базе.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Получаем текущий номер для проверки уникальности
        Optional<Room> existingRoomOpt = roomRepository.findById(id);
        if (existingRoomOpt.isPresent()) {
            Room existingRoom = existingRoomOpt.get();

            // Проверяем уникальность номера комнаты (исключая текущую комнату)
            List<Room> hotelRooms = roomRepository.findByHotelId(existingRoom.getHotel().getId());
            boolean roomNumberExists = hotelRooms.stream()
                    .anyMatch(r -> r.getNumber().equals(roomDetails.getNumber()) &&
                            !r.getId().equals(id));

            if (roomNumberExists) {
                return ResponseEntity.badRequest()
                        .body("ОШИБКА! Номер '" + roomDetails.getNumber() + "' присутствует в базе.");
            }
        }

        roomDetails.setId(id);
        Room updatedRoom = roomRepository.save(roomDetails);
        System.out.println("Обновлен номер с ID: " + id + ": " + updatedRoom);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            // Проверяем, есть ли активные бронирования для этого номера
            Optional<Room> roomOpt = roomRepository.findById(id);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                boolean hasActiveBookings = room.getBookings().stream()
                        .anyMatch(booking -> !"CANCELLED".equals(booking.getStatus()) &&
                                !"COMPLETED".equals(booking.getStatus()));

                if (hasActiveBookings) {
                    System.out.println("Нельзя удалить номер с ID: " + id + ": есть активные брони.");
                    return ResponseEntity.badRequest().build();
                }
            }

            roomRepository.deleteById(id);
            System.out.println("Удален номер с ID: " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable Long hotelId) {
        List<Room> hotelRooms = roomRepository.findByHotelId(hotelId);
        System.out.println("Найдено номеров для отеля " + hotelId + ": " + hotelRooms.size());
        return hotelRooms;
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = roomRepository.findByAvailableTrue();
        System.out.println("Свободные номера: " + availableRooms.size());
        return availableRooms;
    }

    @GetMapping("/hotel/{hotelId}/available")
    public List<Room> getAvailableRoomsByHotel(@PathVariable Long hotelId) {
        List<Room> availableRooms = roomRepository.findAvailableRoomsByHotel(hotelId);
        System.out.println("Доступные номера для отеля " + hotelId + ": " + availableRooms.size());
        return availableRooms;
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Room> updateRoomAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setAvailable(available);
            Room updatedRoom = roomRepository.save(room);
            System.out.println("Освободился номера с ID " + id + ": " + available);
            return ResponseEntity.ok(updatedRoom);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Новый endpoint для поиска доступных номеров на определенные даты
    @GetMapping("/available-for-dates")
    public List<Room> getAvailableRoomsForDates(@RequestParam String checkIn, @RequestParam String checkOut) {
        // Преобразуем строки в LocalDate
        java.time.LocalDate checkInDate = java.time.LocalDate.parse(checkIn);
        java.time.LocalDate checkOutDate = java.time.LocalDate.parse(checkOut);

        List<Room> availableRooms = roomRepository.findAvailableRoomsForDates(checkInDate, checkOutDate);
        System.out.println("Свободные номера на даты " + checkIn + " - " + checkOut + ": " + availableRooms.size());
        return availableRooms;
    }

    @GetMapping("/search")
    public List<Room> searchAvailableRooms(
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minCapacity) {

        System.out.println("Поиск номеров с фильтрами: " +
                "checkIn=" + checkIn + ", checkOut=" + checkOut +
                ", type=" + type + ", maxPrice=" + maxPrice + ", minCapacity=" + minCapacity);

        List<Room> availableRooms;

        if (checkIn != null && checkOut != null) {
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);
            availableRooms = roomRepository.findAvailableRoomsForDates(checkInDate, checkOutDate);
        } else {
            availableRooms = roomRepository.findByAvailableTrue();
        }

        // Применяем фильтры
        List<Room> filteredRooms = availableRooms.stream()
                .filter(room -> type == null || room.getType().equalsIgnoreCase(type))
                .filter(room -> maxPrice == null || room.getPricePerNight() <= maxPrice)
                .filter(room -> minCapacity == null || room.getCapacity() >= minCapacity)
                .collect(Collectors.toList());

        System.out.println("Найдено номеров: " + filteredRooms.size());
        return filteredRooms;
    }

    @PostMapping("/batch-availability")
    @Transactional
    public ResponseEntity<?> updateRoomsAvailability(@RequestBody AvailabilityRequest request) {
        try {
            System.out.println("Массовое обновление доступности для " + request.getRoomIds().size() + " номеров");

            List<Room> updatedRooms = new ArrayList<>();

            for (Long roomId : request.getRoomIds()) {
                Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Номер не найден: " + roomId));

                room.setAvailable(request.isAvailable());
                roomRepository.save(room);
                updatedRooms.add(room);
            }

            System.out.println("Обновлена доступность для " + updatedRooms.size() + " номеров");
            return ResponseEntity.ok(Map.of(
                    "message", "Обновлено номеров: " + updatedRooms.size(),
                    "updatedRooms", updatedRooms.size(),
                    "available", request.isAvailable()
            ));

        } catch (Exception e) {
            System.out.println("Ошибка массового обновления: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}