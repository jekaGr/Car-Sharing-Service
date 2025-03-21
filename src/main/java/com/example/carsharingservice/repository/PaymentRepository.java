package com.example.carsharingservice.repository;

import com.example.carsharingservice.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByRental_UserId(Long userId, Pageable pageable);

    Optional<Payment> findByRentalId(Long id);

    boolean existsByRentalId(Long id);
}
