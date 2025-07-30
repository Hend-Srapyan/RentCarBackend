package com.example.rentcar.repository;

import com.example.rentcar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("""
    SELECT b FROM Booking b
    WHERE b.vehicle.id = :vehicleId
      AND b.status = 'ACTIVE'
      AND b.dateFrom < :newDateTo
      AND b.dateTo > :newDateFrom
""")
    List<Booking> findOverlappingActiveBookings(
            @Param("vehicleId") int vehicleId,
            @Param("newDateFrom") Date newDateFrom,
            @Param("newDateTo") Date newDateTo
    );
}