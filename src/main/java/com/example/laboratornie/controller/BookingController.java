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
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Map<String, Object> bookingDetails) {
        try {
            System.out.println("Обновление бронирования ID: " + id + ", данные: " + bookingDetails);

            // Проверяем существование бронирования
            Optional<Booking> existingBookingOpt = bookingRepository.findById(id);
            if (existingBookingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking existingBooking = existingBookingOpt.get();

            // Обновляем поля
            if (bookingDetails.containsKey("checkInDate")) {
                existingBooking.setCheckInDate(LocalDate.parse(bookingDetails.get("checkInDate").toString()));
            }
            if (bookingDetails.containsKey("checkOutDate")) {
                existingBooking.setCheckOutDate(LocalDate.parse(bookingDetails.get("checkOutDate").toString()));
            }
            if (bookingDetails.containsKey("status")) {
                existingBooking.setStatus(bookingDetails.get("status").toString());
            }
            if (bookingDetails.containsKey("totalPrice")) {
                existingBooking.setTotalPrice(Double.valueOf(bookingDetails.get("totalPrice").toString()));
            }

            // Обновляем гостя если указан
            if (bookingDetails.containsKey("guest") && bookingDetails.get("guest") instanceof Map) {
                Map<String, Object> guestMap = (Map<String, Object>) bookingDetails.get("guest");
                if (guestMap.containsKey("id")) {
                    Long guestId = Long.valueOf(guestMap.get("id").toString());
                    Optional<Guest> guestOpt = guestRepository.findById(guestId);
                    if (guestOpt.isPresent()) {
                        existingBooking.setGuest(guestOpt.get());
                    } else {
                        return ResponseEntity.badRequest().body("Гость с ID " + guestId + " не найден");
                    }
                }
            }

            // Обновляем номер если указан
            if (bookingDetails.containsKey("room") && bookingDetails.get("room") instanceof Map) {
                Map<String, Object> roomMap = (Map<String, Object>) bookingDetails.get("room");
                if (roomMap.containsKey("id")) {
                    Long roomId = Long.valueOf(roomMap.get("id").toString());
                    Optional<Room> roomOpt = roomRepository.findById(roomId);
                    if (roomOpt.isPresent()) {
                        // Проверяем нет ли конфликта бронирований (исключая текущее)
                        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                                        roomId,
                                        existingBooking.getCheckInDate(),
                                        existingBooking.getCheckOutDate()
                                ).stream()
                                .filter(b -> !b.getId().equals(id))
                                .toList();

                        if (!overlappingBookings.isEmpty()) {
                            return ResponseEntity.badRequest().body("Номер занят на указанные даты");
                        }
                        existingBooking.setRoom(roomOpt.get());
                    } else {
                        return ResponseEntity.badRequest().body("Номер с ID " + roomId + " не найден");
                    }
                }
            }

            Booking updatedBooking = bookingRepository.save(existingBooking);
            System.out.println("Обновлена бронь с ID " + id + ": " + updatedBooking);
            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {
            System.err.println("Ошибка при обновлении бронирования: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + e.getMessage());
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