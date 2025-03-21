package com.example.carsharingservice.repository.car;

import com.example.carsharingservice.dto.car.CarSearchParameters;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.repository.car.specification.BrandSpecification;
import com.example.carsharingservice.repository.car.specification.ModelSpecification;
import com.example.carsharingservice.repository.car.specification.TypeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SpecificationBuilderImpl implements SpecificationBuilder<Car> {
    @Autowired
    private SpecificationProviderManager<Car> specificationProviders;

    @Override
    public Specification<Car> build(CarSearchParameters carSearchParametersDto) {
        Specification<Car> spec = Specification.where(null);

        if (carSearchParametersDto.models() != null && carSearchParametersDto.models().length > 0) {
            spec = spec.and(specificationProviders.getSpecificationProvider(
                    ModelSpecification.MODEL).getSpecification(carSearchParametersDto.models()));
        }
        if (carSearchParametersDto.brands() != null && carSearchParametersDto.brands().length > 0) {
            spec = spec.and(specificationProviders.getSpecificationProvider(
                            BrandSpecification.BRAND)
                    .getSpecification(carSearchParametersDto.brands()));
        }
        if (carSearchParametersDto.types() != null && carSearchParametersDto.types().length > 0) {
            spec = spec.and(specificationProviders.getSpecificationProvider(
                    TypeSpecification.TYPE).getSpecification(carSearchParametersDto.types()));
        }
        return spec;
    }
}
