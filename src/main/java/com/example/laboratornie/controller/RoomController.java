package com.example.laboratornie.controller;

import com.example.laboratornie.model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public RoomController() {
        long initialId = counter.incrementAndGet();
        Room initialRoom = new Room();
        initialRoom.setId(initialId);
        initialRoom.setHotelId(1L);
        initialRoom.setNumber("101");
        initialRoom.setType("STANDARD");
        initialRoom.setPricePerNight(2500.0);
        initialRoom.setCapacity(2);
        initialRoom.setAvailable(true);
        rooms.put(initialId, initialRoom);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        long newId = counter.incrementAndGet();
        room.setId(newId);
        rooms.put(newId, room);
        System.out.println("Создан номер: " + room);
        return room;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        System.out.println("Запрошен список всех номеров. Всего: " + rooms.size());
        return new ArrayList<>(rooms.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Room room = rooms.get(id);
        if (room != null) {
            System.out.println("Найден номер по ID " + id + ": " + room);
            return ResponseEntity.ok(room);
        } else {
            System.out.println("Номер с ID " + id + " не найден.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        if (rooms.containsKey(id)) {
            roomDetails.setId(id);
            rooms.put(id, roomDetails);
            System.out.println("Обновлен номер с ID " + id + ": " + roomDetails);
            return ResponseEntity.ok(roomDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (rooms.remove(id) != null) {
            System.out.println("Удален номер с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable Long hotelId) {
        List<Room> hotelRooms = rooms.values().stream()
                .filter(room -> room.getHotelId().equals(hotelId))
                .toList();
        System.out.println("Найдено номеров для отеля " + hotelId + ": " + hotelRooms.size());
        return hotelRooms;
    }
}
