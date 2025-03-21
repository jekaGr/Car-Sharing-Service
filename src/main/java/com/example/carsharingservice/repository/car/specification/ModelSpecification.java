package com.example.carsharingservice.repository.car.specification;

import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.repository.car.SpecificationProvider;
import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ModelSpecification implements SpecificationProvider<Car> {
    public static final String MODEL = "model";

    @Override
    public String getKey() {
        return MODEL;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        Arrays.stream(params).map(param ->
                                        criteriaBuilder.like(root.get(MODEL), "%" + param + "%"))
                                .toArray(Predicate[]::new)
                );
    }
}
