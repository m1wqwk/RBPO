package com.example.laboratornie.controller;

import com.example.laboratornie.model.Booking;
import com.example.laboratornie.model.Guest;
import com.example.laboratornie.model.Payment;
import com.example.laboratornie.model.Room;
import com.example.laboratornie.repository.BookingRepository;
import com.example.laboratornie.repository.GuestRepository;
import com.example.laboratornie.repository.PaymentRepository;
import com.example.laboratornie.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.laboratornie.DTO.BookingRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;

    public BookingController(BookingRepository bookingRepository,
                             GuestRepository guestRepository,
                             RoomRepository roomRepository,
                             PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        if (!guestRepository.existsById(booking.getGuest().getId())) {
            return ResponseEntity.badRequest().body("ОШИБКА! Гость отсутствует в базе!");
        }

        if (!roomRepository.existsById(booking.getRoom().getId())) {
            return ResponseEntity.badRequest().body("ОШИБКА! Номер отсутствует в базе!");
        }

        // Проверка на пересечение броней
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        if (!overlappingBookings.isEmpty()) {
            System.out.println("ОШИБКА! На этот номер уже указанна бронь." + booking.getRoom().getId());
            return ResponseEntity.badRequest().body("Номер забронирован на указанные даты.");
        }

        // Расчет общей стоимости
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                booking.getCheckInDate(), booking.getCheckOutDate());
        double totalPrice = nights * booking.getRoom().getPricePerNight();
        booking.setTotalPrice(totalPrice);

        booking.setStatus("PENDING");
        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("Создана бронь: " + savedBooking);
        return ResponseEntity.ok(savedBooking);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println("Запрошен список всех броней. Всего: " + bookings.size());
        return bookings;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            System.out.println("Найдена бронь по ID: " + id + ": " + booking.get());
            return ResponseEntity.ok(booking.get());
        } else {
            System.out.println("Бронь с ID: " + id + " не найдена.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        if (bookingRepository.existsById(id)) {
            bookingDetails.setId(id);
            Booking updatedBooking = bookingRepository.save(bookingDetails);
            System.out.println("Обновлена бронь с ID " + id + ": " + updatedBooking);
            return ResponseEntity.ok(updatedBooking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            System.out.println("Удалена бронь с ID: " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus("CANCELLED");
            Booking cancelledBooking = bookingRepository.save(booking);
            System.out.println("Бронь с ID: " + id + " отменена");
            return ResponseEntity.ok(cancelledBooking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/guest/{guestId}")
    public List<Booking> getBookingsByGuest(@PathVariable Long guestId) {
        List<Booking> guestBookings = bookingRepository.findByGuestId(guestId);
        System.out.println("Бронь для гостя " + guestId + ": " + guestBookings.size());
        return guestBookings;
    }

    @PostMapping("/full-booking")
    @Transactional
    public ResponseEntity<?> createFullBooking(@RequestBody BookingRequest request) {
        try {
            System.out.println("Начало полного бронирования для: " + request.getGuestEmail());

            // 1. Проверка или создание гостя
            Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                    .orElseGet(() -> {
                        System.out.println("Создание нового гостя: " + request.getGuestEmail());
                        Guest newGuest = new Guest();
                        newGuest.setFirstName(request.getGuestFirstName());
                        newGuest.setLastName(request.getGuestLastName());
                        newGuest.setEmail(request.getGuestEmail());
                        newGuest.setPhone(request.getGuestPhone());
                        newGuest.setPassportNumber(request.getGuestPassport());
                        return guestRepository.save(newGuest);
                    });

            // 2. Проверка доступности номера
            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                    request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());

            if (!overlappingBookings.isEmpty()) {
                System.out.println("Номер занят на указанные даты: " + request.getRoomId());
                return ResponseEntity.badRequest().body("Номер занят на указанные даты");
            }

            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Номер не найден: " + request.getRoomId()));

            // 3. Расчет стоимости
            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    request.getCheckInDate(), request.getCheckOutDate());
            double totalPrice = nights * room.getPricePerNight();

            // 4. Создание бронирования
            Booking booking = new Booking();
            booking.setGuest(guest);
            booking.setRoom(room);
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());
            booking.setTotalPrice(totalPrice);
            booking.setStatus("CONFIRMED");
            Booking savedBooking = bookingRepository.save(booking);

            // 5. Создание платежа
            Payment payment = new Payment();
            payment.setBooking(savedBooking);
            payment.setAmount(totalPrice);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setStatus("COMPLETED");
            paymentRepository.save(payment);

            // 6. Обновление доступности номера
            room.setAvailable(false);
            roomRepository.save(room);

            System.out.println("Успешно создано полное бронирование ID: " + savedBooking.getId());
            return ResponseEntity.ok(savedBooking);

        } catch (Exception e) {
            System.out.println("Ошибка при создании бронирования: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel-with-refund")
    @Transactional
    public ResponseEntity<?> cancelBookingWithRefund(@PathVariable Long id) {
        try {
            System.out.println("Отмена бронирования с возвратом: " + id);

            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено: " + id));

            if ("CANCELLED".equals(booking.getStatus())) {
                return ResponseEntity.badRequest().body("Бронирование уже отменено");
            }

            // 1. Отмена бронирования
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            // 2. Освобождение номера
            Room room = booking.getRoom();
            room.setAvailable(true);
            roomRepository.save(room);

            // 3. Создание возврата платежа
            Payment refund = new Payment();
            refund.setBooking(booking);
            refund.setAmount(-booking.getTotalPrice());
            refund.setPaymentDate(LocalDateTime.now());
            refund.setPaymentMethod("REFUND");
            refund.setStatus("COMPLETED");
            paymentRepository.save(refund);

            System.out.println("Бронирование отменено с возвратом: " + id);
            return ResponseEntity.ok(Map.of(
                    "message", "Бронирование отменено, возврат оформлен",
                    "refundAmount", -booking.getTotalPrice(),
                    "bookingId", id
            ));

        } catch (Exception e) {
            System.out.println("Ошибка при отмене бронирования: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}