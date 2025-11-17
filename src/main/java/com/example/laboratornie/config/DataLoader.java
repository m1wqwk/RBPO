package com.example.laboratornie.config;

import com.example.laboratornie.repository.*;
import com.example.laboratornie.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    public DataLoader(HotelRepository hotelRepository,
                      GuestRepository guestRepository,
                      RoomRepository roomRepository,
                      BookingRepository bookingRepository,
                      PaymentRepository paymentRepository,
                      UserService userService) {
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже данные в базе
        if (hotelRepository.count() > 0) {
            System.out.println("База данных уже инициализирована, пропускаем загрузку тестовых данных.");
            return;
        }

        System.out.println("Для получения прав пройдите аутентификацию.");

    }
}