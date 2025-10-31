package com.example.laboratornie.controller;

import com.example.laboratornie.model.Guest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/guests")
public class GuestController {
    private final Map<Long, Guest> guests = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public GuestController() {
        long initialId = counter.incrementAndGet();
        Guest initialGuest = new Guest();
        initialGuest.setId(initialId);
        initialGuest.setFirstName("Максим");
        initialGuest.setLastName("Лавров");
        initialGuest.setEmail("oguzokv@example.com");
        initialGuest.setPhone("+7-912-345-67-89");
        initialGuest.setPassportNumber("1234 567890");
        guests.put(initialId, initialGuest);
    }

    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        long newId = counter.incrementAndGet();
        guest.setId(newId);
        guests.put(newId, guest);
        System.out.println("Создан гость: " + guest);
        return guest;
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        System.out.println("Запрошен список всех гостей. Всего: " + guests.size());
        return new ArrayList<>(guests.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Guest guest = guests.get(id);
        if (guest != null) {
            System.out.println("Найден гость по ID " + id + ": " + guest);
            return ResponseEntity.ok(guest);
        } else {
            System.out.println("Гость с ID " + id + " не найден.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        if (guests.containsKey(id)) {
            guestDetails.setId(id);
            guests.put(id, guestDetails);
            System.out.println("Обновлен гость с ID " + id + ": " + guestDetails);
            return ResponseEntity.ok(guestDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        if (guests.remove(id) != null) {
            System.out.println("Удален гость с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}