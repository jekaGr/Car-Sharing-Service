package com.example.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.carsharingservice.model.Rental;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;
    private Rental rentalWithReturnDate;
    private Rental rentalWithoutReturnDate;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/create-for-test-rental.sql"));
        }
    }

    @BeforeEach
    void setUp() {
        rentalWithReturnDate = rentalRepository.findById(2L).orElse(null);
        rentalWithoutReturnDate = rentalRepository.findById(1L).orElse(null);
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
    void findAllByUserIdAndActualReturnDateIsNotNull_shouldReturnCorrectPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Rental> result = rentalRepository.findAllByUserIdAndActualReturnDateIsNotNull(
                pageable, 3L);

        // Then
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().contains(rentalWithReturnDate));
        assertFalse(result.getContent().contains(rentalWithoutReturnDate));
    }

    @Test
    void findAllByUserIdAndActualReturnDateIsNull_shouldReturnCorrectPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Rental> result = rentalRepository.findAllByUserIdAndActualReturnDateIsNull(
                pageable, 3L);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(rentalWithoutReturnDate));
        assertFalse(result.getContent().contains(rentalWithReturnDate));
    }
}
