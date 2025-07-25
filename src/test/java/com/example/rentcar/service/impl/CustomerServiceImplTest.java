package com.example.rentcar.service.impl;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import com.example.rentcar.entity.Customer;
import com.example.rentcar.exception.CustomerNotFoundException;
import com.example.rentcar.mapper.CustomerMapper;
import com.example.rentcar.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerMapper = mock(CustomerMapper.class);
        customerService = new CustomerServiceImpl(customerRepository, customerMapper);
    }

    @Test
    void findAll_ShouldReturnCustomerDtoList() {
        List<Customer> customers = Arrays.asList(new Customer(), new Customer());
        List<CustomerDto> customerDtos = Arrays.asList(new CustomerDto(), new CustomerDto());

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toDtoList(customers)).thenReturn(customerDtos);

        List<CustomerDto> result = customerService.findAll();

        assertEquals(2, result.size());
        verify(customerRepository).findAll();
        verify(customerMapper).toDtoList(customers);
    }

    @Test
    void save_ShouldReturnSavedCustomerDto() {
        SaveCustomerRequest request = new SaveCustomerRequest();
        Customer customer = new Customer();
        Customer savedCustomer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toDto(savedCustomer)).thenReturn(customerDto);

        CustomerDto result = customerService.save(request);

        assertEquals(customerDto, result);
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(savedCustomer);
    }

    @Test
    void deleteById_ShouldDeleteCustomer_WhenExists() {
        int id = 1;
        when(customerRepository.existsById(id)).thenReturn(true);

        customerService.deleteById(id);

        verify(customerRepository).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowException_WhenNotFound() {
        int id = 999;
        when(customerRepository.existsById(id)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(id));

        verify(customerRepository, never()).deleteById(anyInt());
    }

    @Test
    void update_ShouldReturnUpdatedCustomerDto() {
        SaveCustomerRequest request = new SaveCustomerRequest();
        Customer customer = new Customer();
        Customer savedCustomer = new Customer();
        CustomerDto dto = new CustomerDto();

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toDto(savedCustomer)).thenReturn(dto);

        CustomerDto result = customerService.update(request);

        assertEquals(dto, result);
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(savedCustomer);
    }
}
