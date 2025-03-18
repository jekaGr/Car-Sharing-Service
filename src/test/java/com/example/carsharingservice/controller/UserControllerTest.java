package com.example.carsharingservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingservice.dto.role.RoleNameDto;
import com.example.carsharingservice.dto.user.UserResponseDto;
import com.example.carsharingservice.model.Role;
import com.example.carsharingservice.service.user.UserService;
import com.example.carsharingservice.util.user.TestUserUtil;
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
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private UserService userService;

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
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateRoleUser_should_return_UserResponseDto_when_user_and_role_exist() throws Exception {
        // Given
        RoleNameDto roleNameDto = new RoleNameDto();
        roleNameDto.setRoleName(Role.RoleName.MANAGER);

        UserResponseDto expectedResponseDto = TestUserUtil.getUserResponseDto();

        String jsonRequest = objectMapper.writeValueAsString(roleNameDto);

        // When
        MvcResult result = mockMvc.perform(put("/users/{id}/role", 3)
                        .content(jsonRequest)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        UserResponseDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(expectedResponseDto, actual);
    }

    @Test
    @WithUserDetails(value = "email@email.com",
            userDetailsServiceBeanName = "customUserDetailsService")
    void getCurrentUserInfo_should_return_UserResponseDto_when_user_is_authenticated()
            throws Exception {
        // Given
        UserResponseDto expectedResponseDto = TestUserUtil.getUserResponseDto();

        // When & Then
        MvcResult result = mockMvc.perform(get("/users/me")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(expectedResponseDto, actual);
    }
}
