package com.example.laboratornie.config;

import com.example.laboratornie.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public DataLoader(HotelRepository hotelRepository,
                      GuestRepository guestRepository,
                      RoomRepository roomRepository,
                      BookingRepository bookingRepository,
                      PaymentRepository paymentRepository) {
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже данные в базе
        if (hotelRepository.count() > 0) {
            System.out.println("База данных уже инициализирована, пропускаем загрузку тестовых данных");
            return;
        }

        System.out.println("База данных готова к работе. Таблицы созданы автоматически.");
        System.out.println("Для тестирования используйте API endpoints для создания данных");
    }
}