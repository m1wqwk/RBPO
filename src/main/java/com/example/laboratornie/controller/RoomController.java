package com.example.laboratornie.controller;

import com.example.laboratornie.DTO.RoomRequest;
import com.example.laboratornie.model.Room;
import com.example.laboratornie.model.Hotel;
import com.example.laboratornie.repository.RoomRepository;
import com.example.laboratornie.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest roomRequest) {
        try {
            System.out.println("Получен запрос на создание номера: " + roomRequest);

            if (roomRequest.getHotelId() == null) {
                return ResponseEntity.badRequest().body("ID отеля не указан.");
            }

            Optional<Hotel> hotelOpt = hotelRepository.findById(roomRequest.getHotelId());
            if (hotelOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Отель с ID: " + roomRequest.getHotelId() + " отсутствует в базе.");
            }

            Hotel hotel = hotelOpt.get();

            List<Room> existingRooms = roomRepository.findByHotelId(hotel.getId());
            boolean roomNumberExists = existingRooms.stream()
                    .anyMatch(r -> r.getNumber().equals(roomRequest.getNumber()));

            if (roomNumberExists) {
                return ResponseEntity.badRequest()
                        .body("ОШИБКА! Номер '" + roomRequest.getNumber() + "' уже существует.");
            }

            Room room = new Room();
            room.setNumber(roomRequest.getNumber());
            room.setType(roomRequest.getType());
            room.setPricePerNight(roomRequest.getPricePerNight());
            room.setCapacity(roomRequest.getCapacity());
            room.setAvailable(roomRequest.getAvailable());
            room.setHotel(hotel);

            Room savedRoom = roomRepository.save(room);
            System.out.println("Создан номер: " + savedRoom);
            return ResponseEntity.ok(savedRoom);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
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
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomRequest roomRequest) {
        try {
            Optional<Room> existingRoomOpt = roomRepository.findById(id);
            if (existingRoomOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Room existingRoom = existingRoomOpt.get();

            if (roomRequest.getHotelId() == null) {
                return ResponseEntity.badRequest().body("ID отеля не указан.");
            }

            Optional<Hotel> hotelOpt = hotelRepository.findById(roomRequest.getHotelId());
            if (hotelOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Отель с ID: " + roomRequest.getHotelId() + " не найден.");
            }

            Hotel hotel = hotelOpt.get();

            List<Room> hotelRooms = roomRepository.findByHotelId(hotel.getId());
            boolean roomNumberExists = hotelRooms.stream()
                    .anyMatch(r -> r.getNumber().equals(roomRequest.getNumber()) &&
                            !r.getId().equals(id));

            if (roomNumberExists) {
                return ResponseEntity.badRequest()
                        .body("ОШИБКА! Номер '" + roomRequest.getNumber() + "' уже существует.");
            }

            existingRoom.setNumber(roomRequest.getNumber());
            existingRoom.setType(roomRequest.getType());
            existingRoom.setPricePerNight(roomRequest.getPricePerNight());
            existingRoom.setCapacity(roomRequest.getCapacity());
            existingRoom.setAvailable(roomRequest.getAvailable());
            existingRoom.setHotel(hotel);

            Room updatedRoom = roomRepository.save(existingRoom);
            return ResponseEntity.ok(updatedRoom);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
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
            System.out.println("Обновлена доступность номера с ID " + id + ": " + available);
            return ResponseEntity.ok(updatedRoom);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/available-for-dates")
    public List<Room> getAvailableRoomsForDates(@RequestParam String checkIn, @RequestParam String checkOut) {
        java.time.LocalDate checkInDate = java.time.LocalDate.parse(checkIn);
        java.time.LocalDate checkOutDate = java.time.LocalDate.parse(checkOut);

        List<Room> availableRooms = roomRepository.findAvailableRoomsForDates(checkInDate, checkOutDate);
        System.out.println("Свободные номера на даты " + checkIn + " - " + checkOut + ": " + availableRooms.size());
        return availableRooms;
    }
}