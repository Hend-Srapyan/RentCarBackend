package com.example.rentcar.service.impl;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.entity.Booking;
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
    void findAll_ShouldReturnListOfBookingDtos() {
        Booking booking = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(dto));

        List<BookingDto> result = bookingService.findAll();

        assertEquals(1, result.size());
        verify(bookingRepository).findAll();
        verify(bookingMapper).toDtoList(anyList());
    }

    @Test
    void findAll_WithPageable_ShouldReturnPageOfBookingDtos() {
        Booking booking = new Booking();
        BookingDto dto = new BookingDto();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));

        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);
        when(bookingMapper.toDto(booking)).thenReturn(dto);

        Page<BookingDto> result = bookingService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(bookingRepository).findAll(pageable);
        verify(bookingMapper).toDto(booking);
    }

    @Test
    void save_ShouldSaveAndReturnBookingDto() {
        SaveBookingRequest request = new SaveBookingRequest();
        Booking entity = new Booking();
        Booking saved = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingMapper.toEntity(request)).thenReturn(entity);
        when(bookingRepository.save(entity)).thenReturn(saved);
        when(bookingMapper.toDto(saved)).thenReturn(dto);

        BookingDto result = bookingService.save(request);

        assertNotNull(result);
        verify(bookingMapper).toEntity(request);
        verify(bookingRepository).save(entity);
        verify(bookingMapper).toDto(saved);
    }

    @Test
    void update_ShouldUpdateAndReturnBookingDto() {
        SaveBookingRequest request = new SaveBookingRequest();
        Booking entity = new Booking();
        Booking updated = new Booking();
        BookingDto dto = new BookingDto();

        when(bookingMapper.toEntity(request)).thenReturn(entity);
        when(bookingRepository.save(entity)).thenReturn(updated);
        when(bookingMapper.toDto(updated)).thenReturn(dto);

        BookingDto result = bookingService.update(request);

        assertNotNull(result);
        verify(bookingMapper).toEntity(request);
        verify(bookingRepository).save(entity);
        verify(bookingMapper).toDto(updated);
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
}
