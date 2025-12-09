package com.example.laboratornie.controller;

import com.example.laboratornie.DTO.PaymentRequest;
import com.example.laboratornie.model.Booking;
import com.example.laboratornie.model.Payment;
import com.example.laboratornie.repository.PaymentRepository;
import com.example.laboratornie.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.laboratornie.DTO.DailyReport;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentController(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            System.out.println("Создание платежа: " + paymentRequest);

            if (!bookingRepository.existsById(paymentRequest.getBookingId())) {
                return ResponseEntity.badRequest().body("Бронирование с ID: " + paymentRequest.getBookingId() + " отсутствует в базе.");
            }

            if (paymentRequest.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Некорректная сумма платежа.");
            }

            Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(paymentRequest.getAmount());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setStatus(paymentRequest.getStatus() != null ? paymentRequest.getStatus() : "PENDING");

            Payment savedPayment = paymentRepository.save(payment);
            System.out.println("Создан платеж: " + savedPayment);
            return ResponseEntity.ok(savedPayment);

        } catch (Exception e) {
            System.err.println("Ошибка при создании платежа: " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        System.out.println("Запрошен список всех платежей. Всего: " + payments.size());
        return payments;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent()) {
            System.out.println("Найден платеж по ID: " + id + ": " + payment.get());
            return ResponseEntity.ok(payment.get());
        } else {
            System.out.println("Платеж с ID: " + id + " отсутствует в базе.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @RequestBody PaymentRequest paymentRequest) {
        try {
            System.out.println("Обновление платежа ID: " + id + ", данные: " + paymentRequest);

            if (!paymentRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            if (!bookingRepository.existsById(paymentRequest.getBookingId())) {
                return ResponseEntity.badRequest().body("Бронирование с ID: " + paymentRequest.getBookingId() + " отсутствует в базе.");
            }

            if (paymentRequest.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Некорректная сумма платежа.");
            }

            Payment existingPayment = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Платеж не найден"));

            Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

            existingPayment.setBooking(booking);
            existingPayment.setAmount(paymentRequest.getAmount());
            existingPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
            existingPayment.setStatus(paymentRequest.getStatus());

            Payment updatedPayment = paymentRepository.save(existingPayment);
            System.out.println("Обновлен платеж с ID: " + id + ": " + updatedPayment);
            return ResponseEntity.ok(updatedPayment);

        } catch (Exception e) {
            System.err.println("Ошибка при обновлении платежа: " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            System.out.println("Удален платеж с ID: " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("COMPLETED");
            payment.setPaymentDate(LocalDateTime.now());
            Payment completedPayment = paymentRepository.save(payment);
            System.out.println("Платеж с ID: " + id + " отмечен как завершенный.");
            return ResponseEntity.ok(completedPayment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<Payment> failPayment(@PathVariable Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("FAILED");
            Payment failedPayment = paymentRepository.save(payment);
            System.out.println("Платеж с ID: " + id + " отмечен как неудачный");
            return ResponseEntity.ok(failedPayment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("REFUNDED");
            Payment refundedPayment = paymentRepository.save(payment);
            System.out.println("Платеж с ID: " + id + " отмечен как возвращенный");
            return ResponseEntity.ok(refundedPayment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/booking/{bookingId}")
    public List<Payment> getPaymentsByBooking(@PathVariable Long bookingId) {
        List<Payment> bookingPayments = paymentRepository.findByBookingId(bookingId);
        System.out.println("Найдено платежей для брони " + bookingId + ": " + bookingPayments.size());
        return bookingPayments;
    }

    @GetMapping("/status/{status}")
    public List<Payment> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        System.out.println("Найдено платежей со статусом '" + status + "': " + payments.size());
        return payments;
    }

    @GetMapping("/stats/summary")
    public ResponseEntity<?> getPaymentSummary() {
        List<Payment> allPayments = paymentRepository.findAll();

        double totalCompleted = allPayments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        double totalPending = allPayments.stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        long completedCount = allPayments.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .count();

        long pendingCount = allPayments.stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .count();

        var summary = new PaymentSummary(totalCompleted, totalPending, completedCount, pendingCount);
        return ResponseEntity.ok(summary);
    }

    private static class PaymentSummary {
        public final double totalCompleted;
        public final double totalPending;
        public final long completedCount;
        public final long pendingCount;

        public PaymentSummary(double totalCompleted, double totalPending, long completedCount, long pendingCount) {
            this.totalCompleted = totalCompleted;
            this.totalPending = totalPending;
            this.completedCount = completedCount;
            this.pendingCount = pendingCount;
        }
    }

    @GetMapping("/reports/daily")
    public ResponseEntity<?> getDailyFinancialReport(@RequestParam String date) {
        try {
            System.out.println("Формирование ежедневного отчета за: " + date);

            LocalDate reportDate = LocalDate.parse(date);
            LocalDateTime startOfDay = reportDate.atStartOfDay();
            LocalDateTime endOfDay = reportDate.atTime(23, 59, 59);

            List<Payment> dailyPayments = paymentRepository.findByPaymentDateBetween(startOfDay, endOfDay);

            double totalIncome = dailyPayments.stream()
                    .filter(p -> p.getAmount() > 0 && "COMPLETED".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            double totalRefunds = dailyPayments.stream()
                    .filter(p -> p.getAmount() < 0 && "COMPLETED".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            List<Booking> dailyBookings = bookingRepository.findByCheckInDate(reportDate);
            long completedBookings = dailyBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .count();

            DailyReport report = new DailyReport(
                    reportDate, totalIncome, totalRefunds,
                    dailyPayments.size(), completedBookings
            );

            System.out.println("Отчет сформирован: доход=" + totalIncome +
                    ", возвраты=" + totalRefunds +
                    ", транзакций=" + dailyPayments.size());

            return ResponseEntity.ok(report);

        } catch (Exception e) {
            System.out.println("Ошибка формирования отчета: " + e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка формирования отчета: " + e.getMessage());
        }
    }
}