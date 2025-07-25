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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    void findAll_ShouldReturnVehicleDtoList() {
        List<Vehicle> vehicles = Collections.singletonList(new Vehicle());
        List<VehicleDto> dtos = Collections.singletonList(new VehicleDto());

        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(vehicleMapper.toDtoList(vehicles)).thenReturn(dtos);

        List<VehicleDto> result = vehicleService.findAll();

        assertEquals(1, result.size());
        verify(vehicleRepository).findAll();
        verify(vehicleMapper).toDtoList(vehicles);
    }

    @Test
    void save_ShouldSaveVehicleWithFile() throws Exception {
        SaveVehicleRequest request = new SaveVehicleRequest();
        request.setImage(null);

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");

        Vehicle entity = new Vehicle();
        Vehicle saved = new Vehicle();
        VehicleDto dto = new VehicleDto();

        when(vehicleMapper.toEntity(request)).thenReturn(entity);
        when(vehicleRepository.save(entity)).thenReturn(saved);
        when(vehicleMapper.toDto(saved)).thenReturn(dto);

        VehicleDto result = vehicleService.save(request, multipartFile);

        assertNotNull(result);
        assertTrue(request.getImage() != null && request.getImage().contains("image.jpg"));
        verify(multipartFile).transferTo(any(File.class));
        verify(vehicleRepository).save(entity);
        verify(vehicleMapper).toDto(saved);
    }

    @Test
    void deleteById_ShouldDelete_WhenExists() {
        when(vehicleRepository.existsById(1)).thenReturn(true);

        vehicleService.deleteById(1);

        verify(vehicleRepository).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrow_WhenNotExists() {
        when(vehicleRepository.existsById(99)).thenReturn(false);

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.deleteById(99));
        verify(vehicleRepository, never()).deleteById(anyInt());
    }

    @Test
    void update_ShouldUpdateVehicleWithFile() throws Exception {
        SaveVehicleRequest request = new SaveVehicleRequest();
        request.setId(1);
        request.setBrand("Toyota");
        request.setModel("Corolla");
        request.setYear(2020);
        request.setColor("Red");
        request.setRegNo("123ABC");
        request.setDailyRate(45);

        Vehicle existing = new Vehicle();
        existing.setId(1);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(existing));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("newpic.png");

        Vehicle saved = new Vehicle();
        VehicleDto dto = new VehicleDto();
        when(vehicleRepository.save(existing)).thenReturn(saved);
        when(vehicleMapper.toDto(saved)).thenReturn(dto);

        VehicleDto result = vehicleService.update(request, multipartFile);

        assertNotNull(result);
        assertEquals("Toyota", existing.getBrand());
        assertTrue(existing.getImage().contains("newpic.png"));
        verify(multipartFile).transferTo(any(File.class));
    }

    @Test
    void update_ShouldThrow_WhenVehicleNotFound() {
        SaveVehicleRequest request = new SaveVehicleRequest();
        request.setId(123);

        when(vehicleRepository.findById(123)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.update(request, multipartFile));
    }
}
