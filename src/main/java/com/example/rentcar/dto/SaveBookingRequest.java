package com.example.rentcar.dto;

import com.example.rentcar.entity.Customer;
import com.example.rentcar.entity.Status;
import com.example.rentcar.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveBookingRequest {

    private int id;
    private Customer customer;
    private Vehicle vehicle;
    private Date dateFrom;
    private Date dateTo;
    private int total;
    private Status status;
}