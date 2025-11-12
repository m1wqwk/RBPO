package com.example.laboratornie.repository;

import com.example.laboratornie.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByStatus(String status);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    List<Payment> findByPaymentDateBetween(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}