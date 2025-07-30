package com.example.rentcar.service.impl;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.entity.Booking;
import com.example.rentcar.exception.BookingNotFoundException;
import com.example.rentcar.mapper.BookingMapper;
import com.example.rentcar.repository.BookingRepository;
import com.example.rentcar.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public Page<BookingDto> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(bookingMapper::toDto);
    }

    @Override
    public BookingDto save(SaveBookingRequest bookingRequest) {
        int vehicleId = bookingRequest.getVehicle().getId();
        Date newDateFrom = bookingRequest.getDateFrom();
        Date newDateTo = bookingRequest.getDateTo();

        List<Booking> overlappingBookings = bookingRepository.findOverlappingActiveBookings(
                vehicleId, newDateFrom, newDateTo);

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("Vehicle is already booked during the requested period");
        }

        Booking booking = bookingRepository.save(bookingMapper.toEntity(bookingRequest));
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking not found with " + id + " id");
        }
        bookingRepository.deleteById(id);
    }

    @Override
    public BookingDto update(SaveBookingRequest bookingRequest) {
        Booking booking = bookingRepository.save(bookingMapper.toEntity(bookingRequest));
        return bookingMapper.toDto(booking);
    }
}