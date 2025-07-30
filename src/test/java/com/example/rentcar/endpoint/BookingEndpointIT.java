package com.example.rentcar.endpoint;

import com.example.rentcar.entity.Booking;
import com.example.rentcar.entity.Customer;
import com.example.rentcar.entity.Status;
import com.example.rentcar.entity.Vehicle;
import com.example.rentcar.repository.BookingRepository;
import com.example.rentcar.repository.CustomerRepository;
import com.example.rentcar.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class BookingEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Customer savedCustomer;
    private Vehicle savedVehicle;

    @BeforeEach
    public void setup() {
        bookingRepository.deleteAll();
        customerRepository.deleteAll();
        vehicleRepository.deleteAll();

        savedCustomer = customerRepository.save(Customer.builder()
                .name("John Doe")
                .city("Yerevan")
                .mobile("123456789")
                .email("john@example.com")
                .build());

        savedVehicle = vehicleRepository.save(Vehicle.builder()
                .brand("Toyota")
                .model("Camry")
                .year(2020)
                .color("Red")
                .dailyRate(100)
                .build());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreateBooking() throws Exception {
        String bookingJson = """
            {
                "customer": {"id": %d},
                "vehicle": {"id": %d},
                "dateFrom": "2025-07-28",
                "dateTo": "2025-07-29",
                "total": 100,
                "status": "ACTIVE"
            }
        """.formatted(savedCustomer.getId(), savedVehicle.getId());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.vehicle.id").value(savedVehicle.getId()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetAllBookings() throws Exception {
        Booking booking = Booking.builder()
                .customer(savedCustomer)
                .vehicle(savedVehicle)
                .dateFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-28"))
                .dateTo(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-29"))
                .total(100)
                .status(Status.ACTIVE)
                .build();

        bookingRepository.save(booking);

        mockMvc.perform(get("/bookings")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customer.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.content[0].vehicle.id").value(savedVehicle.getId()))
                .andExpect(jsonPath("$.content[0].total").value(100));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateBooking() throws Exception {
        Booking booking = Booking.builder()
                .customer(savedCustomer)
                .vehicle(savedVehicle)
                .dateFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-28"))
                .dateTo(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-29"))
                .total(100)
                .status(Status.ACTIVE)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        Customer newCustomer = customerRepository.save(Customer.builder()
                .name("Jane Smith")
                .city("Gyumri")
                .mobile("987654321")
                .email("jane@example.com")
                .build());

        Vehicle newVehicle = vehicleRepository.save(Vehicle.builder()
                .brand("Honda")
                .model("Civic")
                .year(2021)
                .color("Blue")
                .dailyRate(150)
                .build());

        String updateJson = """
            {
                "id": %d,
                "customer": {"id": %d},
                "vehicle": {"id": %d},
                "dateFrom": "2025-08-01",
                "dateTo": "2025-08-02",
                "total": 150,
                "status": "COMPLETED"
            }
        """.formatted(savedBooking.getId(), newCustomer.getId(), newVehicle.getId());

        mockMvc.perform(put("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBooking.getId()))
                .andExpect(jsonPath("$.customer.id").value(newCustomer.getId()))
                .andExpect(jsonPath("$.vehicle.id").value(newVehicle.getId()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.total").value(150));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteBooking() throws Exception {
        Booking booking = Booking.builder()
                .customer(savedCustomer)
                .vehicle(savedVehicle)
                .dateFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-28"))
                .dateTo(new SimpleDateFormat("yyyy-MM-dd").parse("2025-07-29"))
                .total(100)
                .status(Status.ACTIVE)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        mockMvc.perform(delete("/bookings/" + savedBooking.getId()))
                .andExpect(status().isNoContent());

        Optional<Booking> deleted = bookingRepository.findById(savedBooking.getId());
        assertFalse(deleted.isPresent());
    }
}