package com.example.rentcar.service;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.example.rentcar.dto.VehicleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleService {

    Page<VehicleDto> findAll(Pageable pageable);

    VehicleDto save(SaveVehicleRequest vehicleRequest, MultipartFile multipartFile);

    void deleteById(int id);

    VehicleDto update(SaveVehicleRequest vehicleRequest, MultipartFile multipartFile);
}