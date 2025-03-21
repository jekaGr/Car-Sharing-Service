package com.example.carsharingservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalDataReturnRequestDto;
import com.example.carsharingservice.dto.rental.RentalResponseDto;
import com.example.carsharingservice.service.rental.RentalService;
import com.example.carsharingservice.util.rental.TestRentalUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity()).build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/create-for-test-rental.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-all.sql"));
        }
    }

    @Test
    @WithUserDetails(value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void createRental_shouldReturnCreatedRentalResponseDto_whenRentalCreated() throws Exception {
        // Given
        RentalCreateDto createDto = TestRentalUtil.getRentalCreateDto();
        // When
        MvcResult result = mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        RentalResponseDto responseDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), RentalResponseDto.class);
        assertNotNull(responseDto);
        assertEquals(createDto.getCarId(), responseDto.getCarId());
        assertEquals(createDto.getRentalDate(), responseDto.getRentalDate());
        assertEquals(createDto.getReturnDate(), responseDto.getReturnDate());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_shouldReturnRentalResponseDto_whenRentalFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/rentals/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void completeRental_shouldReturnUpdatedRentalResponseDto_whenRentalCompleted()
            throws Exception {
        // Given
        RentalDataReturnRequestDto returnDto = TestRentalUtil.getRentalDataReturnRequestDto();
        // When & Then
        mockMvc.perform(post("/rentals/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnDto)))
                .andExpect(status().isOk());
    }
}
