package com.example.rentcar.endpoint;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import com.example.rentcar.service.CustomerService;
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
public class CustomerEndpoint {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(pageable));
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody @Valid SaveCustomerRequest saveCustomerRequest) {
        return ResponseEntity.ok(customerService.save(saveCustomerRequest));
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable("id") int id) {
        customerService.deleteById(id);
    }

    @PutMapping("/customers")
    public ResponseEntity<CustomerDto> updateBook(@RequestBody SaveCustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.update(customerRequest));
    }
}