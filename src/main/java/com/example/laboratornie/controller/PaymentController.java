package com.example.laboratornie.controller;

import com.example.laboratornie.model.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public PaymentController() {
        long initialId = counter.incrementAndGet();
        Payment initialPayment = new Payment();
        initialPayment.setId(initialId);
        initialPayment.setBookingId(1L);
        initialPayment.setAmount(5000.0);
        initialPayment.setPaymentDate(LocalDateTime.now());
        initialPayment.setPaymentMethod("CARD");
        initialPayment.setStatus("COMPLETED");
        payments.put(initialId, initialPayment);
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        long newId = counter.incrementAndGet();
        payment.setId(newId);
        payment.setPaymentDate(LocalDateTime.now());
        payments.put(newId, payment);
        System.out.println("Создан платеж: " + payment);
        return payment;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        System.out.println("Запрошен список всех платежей. Всего: " + payments.size());
        return new ArrayList<>(payments.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = payments.get(id);
        if (payment != null) {
            System.out.println("Найден платеж по ID " + id + ": " + payment);
            return ResponseEntity.ok(payment);
        } else {
            System.out.println("Платеж с ID " + id + " не найден.");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        if (payments.containsKey(id)) {
            paymentDetails.setId(id);
            payments.put(id, paymentDetails);
            System.out.println("Обновлен платеж с ID " + id + ": " + paymentDetails);
            return ResponseEntity.ok(paymentDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (payments.remove(id) != null) {
            System.out.println("Удален платеж с ID " + id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Long id) {
        Payment payment = payments.get(id);
        if (payment != null) {
            payment.setStatus("COMPLETED");
            payment.setPaymentDate(LocalDateTime.now());
            System.out.println("Платеж с ID " + id + " отмечен как завершенный");
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/booking/{bookingId}")
    public List<Payment> getPaymentsByBooking(@PathVariable Long bookingId) {
        List<Payment> bookingPayments = payments.values().stream()
                .filter(payment -> payment.getBookingId().equals(bookingId))
                .toList();
        System.out.println("Найдено платежей для брони " + bookingId + ": " + bookingPayments.size());
        return bookingPayments;
    }
}