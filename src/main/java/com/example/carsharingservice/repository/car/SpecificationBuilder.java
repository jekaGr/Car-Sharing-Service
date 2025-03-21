package com.example.carsharingservice.repository.car;

import com.example.carsharingservice.dto.car.CarSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(CarSearchParameters carSearchParametersDto);
}
