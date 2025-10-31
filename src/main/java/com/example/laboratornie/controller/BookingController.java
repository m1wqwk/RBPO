package com.example.laboratornie.controller;

import com.example.laboratornie.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public BookingController() {
        long initialId = counter.incrementAndGet();
        Booking initialBooking = new Booking();
        initialBooking.setId(initialId);
        initialBooking.setGuestId(1L);
        initialBooking.setRoomId(1L);
        initialBooking.setCheckInDate(LocalDate.now().plusDays(1));
        initialBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        initialBooking.setStatus("CONFIRMED");
        initialBooking.setTotalPrice(5000.0);
        bookings.put(initialId, initialBooking);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        // Проверка на пересечение броней
        boolean hasOverlap = bookings.values().stream()
                .filter(b -> b.getRoomId().equals(booking.getRoomId()))
                .filter(b -> !b.getStatus().equals("CANCELLED"))
                .anyMatch(existingBooking -> isDateOverlap(
                        existingBooking.getCheckInDate(),
                        existingBooking.getCheckOutDate(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate()
                ));

        if (hasOverlap) {
            System.out.println("Ошибка: пересечение броней для номера " + booking.getRoomId());
            return ResponseEntity.badRequest().body("Пересечение броней: номер уже забронирован на указанные даты");
        }

        long newId = counter.incrementAndGet();
        booking.setId(newId);
        booking.setStatus("PENDING"); // Новая бронь всегда в статусе ожидания
        bookings.put(newId, booking);
        System.out.println("Создана бронь: " + booking);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        System.out.println("Запрошен список всех броней. Всего: " + bookings.size());
        return new ArrayList<>(bookings.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookings.get(id);
        if (booking != null) {
            System.out.println("Найдена бронь по ID " + id + ": " + booking);
            return ResponseEntity.ok(booking);
        } else {
            System.out.println("Бронь с ID " + id + " не найдена.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        if (bookings.containsKey(id)) {
            bookingDetails.setId(id);
            bookings.put(id, bookingDetails);
            System.out.println("Обновлена бронь с ID " + id + ": " + bookingDetails);
            return ResponseEntity.ok(bookingDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookings.remove(id) != null) {
            System.out.println("Удалена бронь с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Booking booking = bookings.get(id);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            System.out.println("Бронь с ID " + id + " отменена");
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/guest/{guestId}")
    public List<Booking> getBookingsByGuest(@PathVariable Long guestId) {
        List<Booking> guestBookings = bookings.values().stream()
                .filter(booking -> booking.getGuestId().equals(guestId))
                .toList();
        System.out.println("Найдено броней для гостя " + guestId + ": " + guestBookings.size());
        return guestBookings;
    }

    private boolean isDateOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
