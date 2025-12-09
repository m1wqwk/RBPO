package com.example.laboratornie.controller;

import com.example.laboratornie.model.Guest;
import com.example.laboratornie.repository.GuestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/guests")
public class GuestController {
    private final GuestRepository guestRepository;

    public GuestController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @PostMapping
    public ResponseEntity<?> createGuest(@RequestBody Guest guest) {
        if (guestRepository.findByEmail(guest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("ОШИБКА! Гость с email '" + guest.getEmail() + "' присутствует в базе.");
        }

        if (guestRepository.findByPassportNumber(guest.getPassportNumber()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("ОШИБКА! Гость с номером паспорта '" + guest.getPassportNumber() + "' присутствует в базе.");
        }

        Guest savedGuest = guestRepository.save(guest);
        System.out.println("Новый гость: " + savedGuest);
        return ResponseEntity.ok(savedGuest);
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        System.out.println("Запрошен список всех гостей. Всего: " + guests.size());
        return guests;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Optional<Guest> guest = guestRepository.findById(id);
        if (guest.isPresent()) {
            System.out.println("Найден гость по ID: " + id + ": " + guest.get());
            return ResponseEntity.ok(guest.get());
        } else {
            System.out.println("Гость с ID: " + id + " отсутствует в базе.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        if (!guestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Guest> existingGuestByEmail = guestRepository.findByEmail(guestDetails.getEmail());
        if (existingGuestByEmail.isPresent() && !existingGuestByEmail.get().getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body("ОШИБКА! Другой гость с таким email '" + guestDetails.getEmail() + "' присутствует в базе.");
        }

        Optional<Guest> existingGuestByPassport = guestRepository.findByPassportNumber(guestDetails.getPassportNumber());
        if (existingGuestByPassport.isPresent() && !existingGuestByPassport.get().getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body("ОШИБКА! Другой гость с номером паспорта '" + guestDetails.getPassportNumber() + "' присутствует в базе.");
        }

        guestDetails.setId(id);
        Guest updatedGuest = guestRepository.save(guestDetails);
        System.out.println("Обновлен гость с ID " + id + ": " + updatedGuest);
        return ResponseEntity.ok(updatedGuest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        if (guestRepository.existsById(id)) {
            Optional<Guest> guestOpt = guestRepository.findById(id);
            if (guestOpt.isPresent()) {
                Guest guest = guestOpt.get();
                boolean hasActiveBookings = guest.getBookings().stream()
                        .anyMatch(booking -> !"CANCELLED".equals(booking.getStatus()) &&
                                !"COMPLETED".equals(booking.getStatus()));

                if (hasActiveBookings) {
                    System.out.println("Нельзя удалить гостя с ID " + id + ": есть активная бронь.");
                    return ResponseEntity.badRequest().build();
                }
            }

            guestRepository.deleteById(id);
            System.out.println("Удален гость с ID: " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Guest> getGuestByEmail(@PathVariable String email) {
        Optional<Guest> guest = guestRepository.findByEmail(email);
        if (guest.isPresent()) {
            System.out.println("Найден гость по email " + email + ": " + guest.get());
            return ResponseEntity.ok(guest.get());
        } else {
            System.out.println("Гость с email " + email + " отсутствует в базе.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/passport/{passportNumber}")
    public ResponseEntity<Guest> getGuestByPassport(@PathVariable String passportNumber) {
        Optional<Guest> guest = guestRepository.findByPassportNumber(passportNumber);
        if (guest.isPresent()) {
            System.out.println("Найден гость по номеру паспорта " + passportNumber + ": " + guest.get());
            return ResponseEntity.ok(guest.get());
        } else {
            System.out.println("Гость с номером паспорта " + passportNumber + " отсутствует в базе.");
            return ResponseEntity.notFound().build();
        }
    }
}