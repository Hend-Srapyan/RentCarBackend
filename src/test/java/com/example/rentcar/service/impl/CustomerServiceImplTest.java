package com.example.rentcar.service.impl;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import com.example.rentcar.entity.Customer;
import com.example.rentcar.exception.CustomerNotFoundException;
import com.example.rentcar.mapper.CustomerMapper;
import com.example.rentcar.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        when(customerRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(customer)));
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        Page<CustomerDto> result = customerService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(customerDto, result.getContent().get(0));
        verify(customerRepository).findAll(pageable);
        verify(customerMapper).toDto(customer);
    }

    @Test
    void save_ShouldSaveAndReturnCustomerDto() {
        SaveCustomerRequest request = new SaveCustomerRequest();
        Customer customer = new Customer();
        Customer savedCustomer = new Customer();
        CustomerDto dto = new CustomerDto();

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toDto(savedCustomer)).thenReturn(dto);

        CustomerDto result = customerService.save(request);

        assertNotNull(result);
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(savedCustomer);
    }

    @Test
    void update_ShouldUpdateAndReturnCustomerDto() {
        SaveCustomerRequest request = new SaveCustomerRequest();
        Customer customer = new Customer();
        Customer updated = new Customer();
        CustomerDto dto = new CustomerDto();

        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(updated);
        when(customerMapper.toDto(updated)).thenReturn(dto);

        CustomerDto result = customerService.update(request);

        assertNotNull(result);
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(updated);
    }

    @Test
    void deleteById_ShouldDeleteWhenExists() {
        when(customerRepository.existsById(1)).thenReturn(true);

        customerService.deleteById(1);

        verify(customerRepository).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrowWhenNotExists() {
        when(customerRepository.existsById(42)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(42));
        verify(customerRepository, never()).deleteById(anyInt());
    }
}