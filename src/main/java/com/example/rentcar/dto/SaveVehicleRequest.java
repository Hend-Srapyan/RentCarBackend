package com.example.rentcar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveVehicleRequest {

    private int id;
    private String regNo;
    private String brand;
    private String model;
    private int year;
    private String color;
    private int dailyRate;
    private String image;
}