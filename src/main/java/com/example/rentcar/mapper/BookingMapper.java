package com.example.rentcar.mapper;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.entity.Booking;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toDto(Booking booking);

    List<BookingDto> toDtoList(List<Booking> bookings);

    Booking toEntity(SaveBookingRequest bookingRequest);
}