package com.example.rentcar.service.impl;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.entity.Booking;
import com.example.rentcar.exception.BookingNotFoundException;
import com.example.rentcar.mapper.BookingMapper;
import com.example.rentcar.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingRepository bookingRepository;
    private BookingMapper bookingMapper;
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper);
    }

    @Test
    void testFindAll() {
        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        List<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());

        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toDtoList(bookings)).thenReturn(bookingDtos);

        List<BookingDto> result = bookingService.findAll();

        assertEquals(2, result.size());
        verify(bookingRepository).findAll();
        verify(bookingMapper).toDtoList(bookings);
    }

    @Test
    void testSave() {
        SaveBookingRequest request = new SaveBookingRequest();
        Booking entity = new Booking();
        Booking savedEntity = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingMapper.toEntity(request)).thenReturn(entity);
        when(bookingRepository.save(entity)).thenReturn(savedEntity);
        when(bookingMapper.toDto(savedEntity)).thenReturn(dto);

        BookingDto result = bookingService.save(request);

        assertNotNull(result);
        verify(bookingMapper).toEntity(request);
        verify(bookingRepository).save(entity);
        verify(bookingMapper).toDto(savedEntity);
    }

    @Test
    void testDeleteById_success() {
        int id = 1;
        when(bookingRepository.existsById(id)).thenReturn(true);

        bookingService.deleteById(id);

        verify(bookingRepository).existsById(id);
        verify(bookingRepository).deleteById(id);
    }

    @Test
    void testDeleteById_notFound() {
        int id = 42;
        when(bookingRepository.existsById(id)).thenReturn(false);

        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.deleteById(id)
        );

        assertEquals("Booking not found with 42 id", exception.getMessage());
        verify(bookingRepository).existsById(id);
        verify(bookingRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdate() {
        SaveBookingRequest request = new SaveBookingRequest();
        Booking entity = new Booking();
        Booking savedEntity = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingMapper.toEntity(request)).thenReturn(entity);
        when(bookingRepository.save(entity)).thenReturn(savedEntity);
        when(bookingMapper.toDto(savedEntity)).thenReturn(dto);

        BookingDto result = bookingService.update(request);

        assertNotNull(result);
        verify(bookingMapper).toEntity(request);
        verify(bookingRepository).save(entity);
        verify(bookingMapper).toDto(savedEntity);
    }
}
