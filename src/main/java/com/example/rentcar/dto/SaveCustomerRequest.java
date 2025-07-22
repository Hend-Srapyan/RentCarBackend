package com.example.rentcar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerRequest {

    private int id;
    private String name;
    private String city;
    private String mobile;
    private String email;
}