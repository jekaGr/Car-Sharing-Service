package com.example.carsharingservice.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.dto.car.CarSearchParameters;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.service.car.CarService;
import com.example.carsharingservice.util.car.TestCarUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private CarService carService;

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
    @WithMockUser(roles = "MANAGER")
    void createCar_shouldReturnCreatedCarResponseDto_whenCarCreated() throws Exception {
        // Given
        CarCreateDto createDto = TestCarUtil.getCarCreateDto();
        // When
        MvcResult result = mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        CarResponseDto responseDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), CarResponseDto.class);

        Car createdCar = TestCarUtil.createForSavingCar();
        assertNotNull(createdCar);
        assertEquals(createDto.getModel(), createdCar.getModel());
        assertEquals(createDto.getBrand(), createdCar.getBrand());
        assertEquals(createDto.getType(), createdCar.getType());
        assertEquals(createDto.getInventory(), createdCar.getInventory());
        assertEquals(createDto.getDailyFee(), createdCar.getDailyFee());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void searchCars_shouldReturnCarResponseDtoList_whenCarsFound() throws Exception {
        // Given
        CarSearchParameters searchParameters = new CarSearchParameters(
                new String[]{"X6","X5"},
                new String[]{"BMW"},
                new String[]{"SEDAN","SUV"}
        );

        CarResponseDto car1 = TestCarUtil.getCarResponseDto();
        CarResponseDto car2 = new CarResponseDto().setId(2L).setModel("X5")
                .setBrand("BMW").setInventory(3).setType(Car.Type.SUV)
                .setDailyFee(new BigDecimal("130"));
        List<CarResponseDto> expectedList = Arrays.asList(car1, car2);

        // When & Then
        MvcResult result = mockMvc.perform(get("/cars/search")
                        .param("brands", searchParameters.brands())
                        .param("models", searchParameters.models())
                        .param("types", searchParameters.types())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarResponseDto> actualList = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<List<CarResponseDto>>() {});

        assertEquals(expectedList, actualList);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getCarById_ValidId_ShouldReturnBookDto() throws Exception {
        // Given
        CarResponseDto expected = TestCarUtil.getCarResponseDto();
        // When
        MvcResult result = mockMvc.perform(get("/cars/{id}",1L)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @Transactional
    void delete_ValidId_ShouldReturnNoContent() throws Exception {
        // Given
        Long expectedId = 1L;
        // When
        mockMvc.perform(delete("/cars/{id}", expectedId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        // Then
        assertThrows(EntityNotFoundException.class, () -> carService.findById(expectedId));
    }
}
