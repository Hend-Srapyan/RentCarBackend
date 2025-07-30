package com.example.rentcar.endpoint;

import com.example.rentcar.dto.SaveCustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private SaveCustomerRequest defaultCustomerRequest;

    @BeforeEach
    public void setup() {
        defaultCustomerRequest = SaveCustomerRequest.builder()
                .id(0)
                .name("John")
                .city("Yerevan")
                .mobile("+37491234567")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", is(notNullValue())));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testAddCustomer() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultCustomerRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.city").value("Yerevan"))
                .andExpect(jsonPath("$.mobile").value("+37491234567"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateCustomer() throws Exception {
        String createResponse = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultCustomerRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int createdId = objectMapper.readTree(createResponse).get("id").asInt();


        SaveCustomerRequest updateRequest = SaveCustomerRequest.builder()
                .id(createdId)
                .name("Jane")
                .city("Gyumri")
                .mobile("+37498765432")
                .email("jane.smith@example.com")
                .build();

        mockMvc.perform(put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.city").value("Gyumri"))
                .andExpect(jsonPath("$.mobile").value("+37498765432"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteCustomer() throws Exception {

        String createResponse = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultCustomerRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int createdId = objectMapper.readTree(createResponse).get("id").asInt();

        mockMvc.perform(delete("/customers/" + createdId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}