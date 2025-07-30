package com.example.rentcar.service.impl;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.entity.Booking;
import com.example.rentcar.entity.Vehicle;
import com.example.rentcar.exception.BookingNotFoundException;
import com.example.rentcar.mapper.BookingMapper;
import com.example.rentcar.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void findAll_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Booking booking = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        Page<BookingDto> result = bookingService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
        verify(bookingRepository).findAll(pageable);
        verify(bookingMapper).toDto(booking);
    }

    @Test
    void save_ShouldSaveAndReturnDto_WhenNoOverlap() {
        SaveBookingRequest request = mock(SaveBookingRequest.class);
        Vehicle vehicleMock = mock(Vehicle.class);

        Date dateFrom = new Date();
        Date dateTo = new Date(dateFrom.getTime() + 86400000); // +1 день

        when(vehicleMock.getId()).thenReturn(1);
        when(request.getVehicle()).thenReturn(vehicleMock);
        when(request.getDateFrom()).thenReturn(dateFrom);
        when(request.getDateTo()).thenReturn(dateTo);

        Booking booking = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingRepository.findOverlappingActiveBookings(1, dateFrom, dateTo))
                .thenReturn(Collections.emptyList());
        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        BookingDto result = bookingService.save(request);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(bookingRepository).findOverlappingActiveBookings(1, dateFrom, dateTo);
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toEntity(request);
        verify(bookingMapper).toDto(booking);
    }

    @Test
    void save_ShouldThrow_WhenOverlapExists() {
        SaveBookingRequest request = mock(SaveBookingRequest.class);
        Vehicle vehicleMock = mock(Vehicle.class);

        Date dateFrom = new Date();
        Date dateTo = new Date(dateFrom.getTime() + 86400000);

        when(vehicleMock.getId()).thenReturn(1);
        when(request.getVehicle()).thenReturn(vehicleMock);
        when(request.getDateFrom()).thenReturn(dateFrom);
        when(request.getDateTo()).thenReturn(dateTo);

        Booking overlapping = new Booking();

        when(bookingRepository.findOverlappingActiveBookings(1, dateFrom, dateTo))
                .thenReturn(List.of(overlapping));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bookingService.save(request));
        assertEquals("Vehicle is already booked during the requested period", ex.getMessage());

        verify(bookingRepository).findOverlappingActiveBookings(1, dateFrom, dateTo);
        verify(bookingRepository, never()).save(any());
    }


    @Test
    void deleteById_ShouldDelete_WhenExists() {
        when(bookingRepository.existsById(1)).thenReturn(true);

        bookingService.deleteById(1);

        verify(bookingRepository).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrow_WhenNotExists() {
        when(bookingRepository.existsById(42)).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteById(42));
        verify(bookingRepository, never()).deleteById(anyInt());
    }

    @Test
    void update_ShouldSaveAndReturnDto() {
        SaveBookingRequest request = new SaveBookingRequest();
        Booking booking = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        BookingDto result = bookingService.update(request);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(bookingMapper).toEntity(request);
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toDto(booking);
    }
}
