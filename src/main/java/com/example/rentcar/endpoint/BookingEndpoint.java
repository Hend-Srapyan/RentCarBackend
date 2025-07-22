package com.example.rentcar.endpoint;

import com.example.rentcar.dto.BookingDto;
import com.example.rentcar.dto.SaveBookingRequest;
import com.example.rentcar.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookingEndpoint {

    private final BookingService bookingService;

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
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