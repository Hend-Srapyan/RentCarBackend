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
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void findAll_ShouldReturnListOfCustomerDtos() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
        when(customerMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(dto));

        List<CustomerDto> result = customerService.findAll();

        assertEquals(1, result.size());
        verify(customerRepository).findAll();
        verify(customerMapper).toDtoList(anyList());
    }

    @Test
    void findAll_WithPageable_ShouldReturnPageOfCustomerDtos() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(Collections.singletonList(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDto(customer)).thenReturn(dto);

        Page<CustomerDto> result = customerService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
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
