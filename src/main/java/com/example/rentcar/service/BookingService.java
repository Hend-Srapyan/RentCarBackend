package com.example.rentcar.service;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    List<BookingDto> findAll();

    BookingDto save(SaveBookingRequest bookingRequest);

    void deleteById(int id);

    BookingDto update(SaveBookingRequest bookingRequest);

    Page<BookingDto> findAll(Pageable pageable);
}