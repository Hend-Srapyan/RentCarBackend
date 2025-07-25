package com.example.rentcar.service.impl;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import com.example.rentcar.entity.Customer;
import com.example.rentcar.exception.CustomerNotFoundException;
import com.example.rentcar.mapper.CustomerMapper;
import com.example.rentcar.repository.CustomerRepository;
import com.example.rentcar.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.toDtoList(customers);
    }

    @Override
    public Page<CustomerDto> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    @Override
    public CustomerDto save(SaveCustomerRequest customerRequest) {
        Customer customer = customerRepository.save(customerMapper.toEntity(customerRequest));
        return customerMapper.toDto(customer);
    }

    @Override
    public void deleteById(int id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with " + id + " id");
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerDto update(SaveCustomerRequest customerRequest) {
        Customer customer = customerRepository.save(customerMapper.toEntity(customerRequest));
        return customerMapper.toDto(customer);
    }
}