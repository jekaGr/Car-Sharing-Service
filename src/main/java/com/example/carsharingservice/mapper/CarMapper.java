package com.example.carsharingservice.mapper;

import com.example.carsharingservice.config.MapperConfig;
import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toCarResponseDto(Car car);

    @Mapping(target = "id", ignore = true)
    Car toCar(CarCreateDto requestDto);

    void updateCarFromDto(CarCreateDto carCreateDto, @MappingTarget Car car);
}
