package com.example.carsharingservice.repository;

import com.example.carsharingservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarRepository extends JpaRepository<Car,Long>, JpaSpecificationExecutor<Car> {
}
