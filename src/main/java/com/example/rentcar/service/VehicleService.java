package com.example.rentcar.service;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.example.rentcar.dto.VehicleDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VehicleService {

    List<VehicleDto> findAll();

    VehicleDto save(SaveVehicleRequest vehicleRequest, MultipartFile multipartFile);

    void deleteById(int id);

    VehicleDto update(SaveVehicleRequest vehicleRequest, MultipartFile multipartFile);
}