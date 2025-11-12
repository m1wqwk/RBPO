package com.example.laboratornie.config;

import com.example.laboratornie.model.*;
import com.example.laboratornie.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        if (hotelRepository.count() > 0) {
            System.out.println("Данные уже существуют, пропускаем инициализацию");
            return;
        }

        Hotel hotel = new Hotel();
        hotel.setName("Отель Элеон");
        hotel.setAddress("Москва, Комсомольская площадь, д.3");
        hotel.setPhone("+7-999-777-77-77");
        hotel.setStars(5);
        Hotel savedHotel = hotelRepository.save(hotel);

        // Создание гостей
        Guest guest1 = new Guest();
        guest1.setFirstName("Лев");
        guest1.setLastName("Глебович");
        guest1.setEmail("levglebov@gmail.com");
        guest1.setPhone("+7-996-997-67-33");
        guest1.setPassportNumber("4945 567891");
        Guest savedGuest1 = guestRepository.save(guest1);

        Guest guest2 = new Guest();
        guest2.setFirstName("Ольга");
        guest2.setLastName("Богдановна");
        guest2.setEmail("olgab@gmail.com");
        guest2.setPhone("+7-912-444-39-21");
        guest2.setPassportNumber("4945 456789");
        Guest savedGuest2 = guestRepository.save(guest2);

        // Создание номеров
        Room room1 = new Room();
        room1.setNumber("203");
        room1.setType("STANDARD");
        room1.setPricePerNight(2500.0);
        room1.setCapacity(2);
        room1.setAvailable(true);
        room1.setHotel(savedHotel);
        Room savedRoom1 = roomRepository.save(room1);

        Room room2 = new Room();
        room2.setNumber("402");
        room2.setType("DELUXE");
        room2.setPricePerNight(5000.0);
        room2.setCapacity(3);
        room2.setAvailable(true);
        room2.setHotel(savedHotel);
        Room savedRoom2 = roomRepository.save(room2);

        Room room3 = new Room();
        room3.setNumber("201");
        room3.setType("SUITE");
        room3.setPricePerNight(7500.0);
        room3.setCapacity(4);
        room3.setAvailable(true);
        room3.setHotel(savedHotel);
        Room savedRoom3 = roomRepository.save(room3);

        // Создание бронирований
        Booking booking1 = new Booking();
        booking1.setGuest(savedGuest1);
        booking1.setRoom(savedRoom1);
        booking1.setCheckInDate(LocalDate.now().plusDays(1));
        booking1.setCheckOutDate(LocalDate.now().plusDays(3));
        booking1.setStatus("CONFIRMED");
        booking1.setTotalPrice(5000.0);
        Booking savedBooking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setGuest(savedGuest2);
        booking2.setRoom(savedRoom2);
        booking2.setCheckInDate(LocalDate.now().plusDays(5));
        booking2.setCheckOutDate(LocalDate.now().plusDays(7));
        booking2.setStatus("PENDING");
        booking2.setTotalPrice(10000.0);
        Booking savedBooking2 = bookingRepository.save(booking2);

        // Создание платежей
        Payment payment1 = new Payment();
        payment1.setBooking(savedBooking1);
        payment1.setAmount(5000.0);
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setPaymentMethod("CARD");
        payment1.setStatus("COMPLETED");
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setBooking(savedBooking2);
        payment2.setAmount(5000.0);
        payment2.setPaymentDate(LocalDateTime.now());
        payment2.setPaymentMethod("CARD");
        payment2.setStatus("PENDING");
        paymentRepository.save(payment2);

        System.out.println("Данные успешно загружены в базу данных!");
    }
}