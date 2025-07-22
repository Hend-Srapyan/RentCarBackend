package com.example.rentcar.mapper;

import com.example.rentcar.dto.CustomerDto;
import com.example.rentcar.dto.SaveCustomerRequest;
import com.example.rentcar.entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

    List<CustomerDto> toDtoList(List<Customer> customers);

    Customer toEntity(SaveCustomerRequest customerRequest);
}