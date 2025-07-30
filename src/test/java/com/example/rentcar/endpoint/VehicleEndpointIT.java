package com.example.rentcar.endpoint;

import com.example.rentcar.dto.SaveVehicleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VehicleEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/vehicles";

    private SaveVehicleRequest defaultVehicleRequest;

    @BeforeEach
    public void setup() {
        defaultVehicleRequest = SaveVehicleRequest.builder()
                .id(0)
                .regNo("ABC123")
                .brand("Toyota")
                .model("Camry")
                .year(2020)
                .color("Red")
                .dailyRate(100)
                .image(null)
                .build();
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllVehicles() throws Exception {
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddVehicle() throws Exception {
        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(defaultVehicleRequest)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateVehicle() throws Exception {

        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(defaultVehicleRequest)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "car.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        String response = mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int createdId = objectMapper.readTree(response).get("id").asInt();


        SaveVehicleRequest updateRequest = SaveVehicleRequest.builder()
                .id(createdId)
                .regNo("UPD123")
                .brand("Honda")
                .model("Civic")
                .year(2021)
                .color("Blue")
                .dailyRate(110)
                .image(null)
                .build();

        MockMultipartFile vehiclePartUpdate = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateRequest)
        );
        MockMultipartFile imagePartUpdate = new MockMultipartFile(
                "image",
                "car-update.jpg",
                "image/jpeg",
                "updated fake image content".getBytes()
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePartUpdate)
                        .file(imagePartUpdate)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Honda"))
                .andExpect(jsonPath("$.model").value("Civic"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteVehicle() throws Exception {

        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(defaultVehicleRequest)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "car.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        String response = mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int createdId = objectMapper.readTree(response).get("id").asInt();


        mockMvc.perform(delete(baseUrl + "/" + createdId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetImage() throws Exception {
        String imageName = "test.jpg";

        mockMvc.perform(get(baseUrl + "/images/" + imageName)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/jpeg"));
    }


    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultVehicleRequest))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddVehicleAllowedForUserRole() throws Exception {
        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(defaultVehicleRequest)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .with(csrf())
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }


    @Test
    public void testAddVehicleForbiddenForAnonymous() throws Exception {
        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(defaultVehicleRequest)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddVehicleValidationError() throws Exception {
        SaveVehicleRequest invalidRequest = SaveVehicleRequest.builder()
                .id(0)
                .regNo("")
                .brand(null)
                .build();

        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteNonExistingVehicle() throws Exception {
        mockMvc.perform(delete(baseUrl + "/999999")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetImage_FileNotFound_ShouldReturnEmptyByteArray() throws Exception {
        String nonExistentImage = "nonexistent.jpg";

        mockMvc.perform(get(baseUrl + "/images/" + nonExistentImage)
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateVehicle_NonExisting_ShouldReturnNotFound() throws Exception {
        SaveVehicleRequest updateRequest = SaveVehicleRequest.builder()
                .id(9999)
                .regNo("NONEXIST")
                .brand("Brand")
                .model("Model")
                .year(2021)
                .color("Color")
                .dailyRate(100)
                .build();

        MockMultipartFile vehiclePart = new MockMultipartFile(
                "vehicle",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(updateRequest)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "car.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart(baseUrl)
                        .file(vehiclePart)
                        .file(imagePart)
                        .with(csrf())
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isNotFound());
    }
}