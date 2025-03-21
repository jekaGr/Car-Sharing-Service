package com.example.carsharingservice.repository;

import com.example.carsharingservice.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental,Long> {

    Page<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Pageable pageable, Long userId);

    Page<Rental> findAllByUserIdAndActualReturnDateIsNull(Pageable pageable, Long userId);
}
