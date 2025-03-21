package com.example.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.carsharingservice.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_shouldReturnRole() {
        assertNotNull(roleRepository.findByName(Role.RoleName.valueOf("CUSTOMER")));
    }
}
