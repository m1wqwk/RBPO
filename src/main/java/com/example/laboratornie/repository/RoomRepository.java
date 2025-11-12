package com.example.laboratornie.repository;

import com.example.laboratornie.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByAvailableTrue();

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.available = true")
    List<Room> findAvailableRoomsByHotel(@Param("hotelId") Long hotelId);

    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
            "(SELECT b.room.id FROM Booking b WHERE " +
            "b.status != 'CANCELLED' AND " +
            "((b.checkInDate BETWEEN :checkIn AND :checkOut) OR " +
            "(b.checkOutDate BETWEEN :checkIn AND :checkOut) OR " +
            "(b.checkInDate <= :checkIn AND b.checkOutDate >= :checkOut)))")
    List<Room> findAvailableRoomsForDates(@Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut);
}