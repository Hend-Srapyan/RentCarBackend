package com.example.rentcar.service;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> findAll();

    CustomerDto save(SaveCustomerRequest customerRequest);

    void deleteById(int id);

    CustomerDto update(SaveCustomerRequest customerRequest);
}