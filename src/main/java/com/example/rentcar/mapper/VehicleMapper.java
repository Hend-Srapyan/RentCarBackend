package com.example.rentcar.mapper;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.example.rentcar.dto.VehicleDto;
import com.example.rentcar.entity.Vehicle;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDto toDto(Vehicle vehicle);

    List<VehicleDto> toDtoList(List<Vehicle> vehicles);

    Vehicle toEntity(SaveVehicleRequest vehicleRequest);
}