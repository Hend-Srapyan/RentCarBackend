package com.example.rentcar.service.impl;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.example.rentcar.dto.VehicleDto;
import com.example.rentcar.entity.Vehicle;
import com.example.rentcar.exception.VehicleNotFoundException;
import com.example.rentcar.mapper.VehicleMapper;
import com.example.rentcar.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void findAll_ShouldReturnPagedVehicleDtos() {
        Vehicle vehicle = new Vehicle();
        VehicleDto dto = new VehicleDto();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehiclePage = new PageImpl<>(Collections.singletonList(vehicle));
        when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(dto);

        Page<VehicleDto> result = vehicleService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(vehicleRepository).findAll(pageable);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void save_ShouldSaveVehicleWithFile() throws Exception {
        SaveVehicleRequest request = new SaveVehicleRequest();
        Vehicle vehicleEntity = new Vehicle();
        Vehicle savedVehicle = new Vehicle();
        VehicleDto dto = new VehicleDto();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");

        when(vehicleMapper.toEntity(request)).thenReturn(vehicleEntity);
        when(vehicleRepository.save(vehicleEntity)).thenReturn(savedVehicle);
        when(vehicleMapper.toDto(savedVehicle)).thenReturn(dto);

        VehicleDto result = vehicleService.save(request, multipartFile);

        assertNotNull(result);
        verify(multipartFile).transferTo(any(File.class));
        verify(vehicleRepository).save(vehicleEntity);
        verify(vehicleMapper).toDto(savedVehicle);
    }

    @Test
    void deleteById_ShouldDeleteWhenExists() {
        when(vehicleRepository.existsById(1)).thenReturn(true);

        vehicleService.deleteById(1);

        verify(vehicleRepository).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrowWhenNotExists() {
        when(vehicleRepository.existsById(42)).thenReturn(false);

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.deleteById(42));
        verify(vehicleRepository, never()).deleteById(anyInt());
    }

    @Test
    void update_ShouldUpdateVehicleWithFile() throws Exception {
        SaveVehicleRequest request = new SaveVehicleRequest();
        request.setId(1);
        request.setBrand("Toyota");
        request.setModel("Corolla");
        request.setYear(2022);
        request.setColor("White");
        request.setRegNo("ABC123");
        request.setDailyRate(50);

        Vehicle existing = new Vehicle();
        Vehicle saved = new Vehicle();
        VehicleDto dto = new VehicleDto();

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("updated.jpg");

        when(vehicleRepository.save(existing)).thenReturn(saved);
        when(vehicleMapper.toDto(saved)).thenReturn(dto);

        VehicleDto result = vehicleService.update(request, multipartFile);

        assertNotNull(result);
        verify(multipartFile).transferTo(any(File.class));
        verify(vehicleRepository).save(existing);
        verify(vehicleMapper).toDto(saved);
        assertEquals("Toyota", existing.getBrand());
        assertEquals("Corolla", existing.getModel());
    }

    @Test
    void update_ShouldThrowWhenVehicleNotFound() {
        SaveVehicleRequest request = new SaveVehicleRequest();
        request.setId(99);

        when(vehicleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.update(request, multipartFile));
    }
}