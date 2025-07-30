package com.example.rentcar.service;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingDto save(SaveBookingRequest bookingRequest);

    void deleteById(int id);

    BookingDto update(SaveBookingRequest bookingRequest);

    Page<BookingDto> findAll(Pageable pageable);
}