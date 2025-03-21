package com.example.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.carsharingservice.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) throws SQLException {
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
    void existsValidUserByEmail_returnTrue() {
        assertTrue(userRepository.existsByEmail("email@email.com"));
    }

    @Test
    void existsInvalidUserByEmail_returnFalse() {
        assertFalse(userRepository.existsByEmail("mail@email.com"));
    }

    @Test
    void findByEmailValidUser_returnUser() {
        User foundUser = userRepository.findByEmailWithRoles("email@email.com").orElse(null);
        assertNotNull(foundUser);
        assertEquals("firstName", foundUser.getFirstName());
        assertEquals("lastName", foundUser.getLastName());
    }

    @Test
    void findByEmailInvalidUser_notFound() {
        Optional<User> foundUser = userRepository.findByEmailWithRoles("invalid@email.com");
        assertTrue(foundUser.isEmpty());
    }
}
