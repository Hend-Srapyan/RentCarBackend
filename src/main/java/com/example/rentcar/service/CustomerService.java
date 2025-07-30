package com.example.rentcar.service;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    Page<CustomerDto> findAll(Pageable pageable);

    CustomerDto save(SaveCustomerRequest customerRequest);

    void deleteById(int id);

    CustomerDto update(SaveCustomerRequest customerRequest);
}