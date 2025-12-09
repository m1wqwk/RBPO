package com.example.laboratornie.config;

import com.example.laboratornie.repository.*;
import com.example.laboratornie.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(HotelRepository hotelRepository,
                      GuestRepository guestRepository,
                      RoomRepository roomRepository,
                      BookingRepository bookingRepository,
                      PaymentRepository paymentRepository,
                      UserService userService,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (hotelRepository.count() > 0) {
            System.out.println("Database is loaded.");
            return;
        }
    }
}