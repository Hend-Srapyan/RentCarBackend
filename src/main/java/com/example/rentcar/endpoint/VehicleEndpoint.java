package com.example.rentcar.endpoint;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.example.rentcar.dto.VehicleDto;
import com.example.rentcar.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VehicleEndpoint {

    private final VehicleService vehicleService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/vehicles")
    public ResponseEntity<Page<VehicleDto>> getAllVehicles(Pageable pageable) {
        return ResponseEntity.ok(vehicleService.findAll(pageable));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDto> addVehicle(@RequestPart("vehicle") @Valid SaveVehicleRequest vehicleRequest,
                                                 @RequestPart("image") MultipartFile multipartFile) {
        return ResponseEntity.ok(vehicleService.save(vehicleRequest, multipartFile));
    }

    @DeleteMapping("/vehicles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable("id") int id) {
        vehicleService.deleteById(id);
    }

    @PutMapping("/vehicles")
    public ResponseEntity<VehicleDto> updateVehicle(@RequestPart("vehicle") SaveVehicleRequest vehicleRequest,
                                                    @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        return ResponseEntity.ok(vehicleService.update(vehicleRequest, multipartFile));
    }

    @SneakyThrows
    @GetMapping(value = "/vehicles/images/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable("imageName") String imageName) {
        File file = new File(uploadPath, imageName);
        if (file.exists()) {
            return Files.readAllBytes(file.toPath());
        }
        return new byte[0];
    }
}