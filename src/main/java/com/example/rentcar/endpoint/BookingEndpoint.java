package com.example.rentcar.endpoint;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingEndpoint {

    private final BookingService bookingService;

    @GetMapping("/bookings")
    public ResponseEntity<Page<BookingDto>> getAllBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.findAll(pageable));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingDto> addBooking(@RequestBody @Valid SaveBookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.save(bookingRequest));
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable("id") int id) {
        bookingService.deleteById(id);
    }

    @PutMapping("/bookings")
    public ResponseEntity<BookingDto> updateBooking(@RequestBody SaveBookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.update(bookingRequest));
    }
}