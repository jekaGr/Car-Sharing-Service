package com.example.carsharingservice.service.car;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.dto.car.CarSearchParameters;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.mapper.CarMapper;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.repository.CarRepository;
import com.example.carsharingservice.repository.car.SpecificationBuilderImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final SpecificationBuilderImpl specificationBuilder;

    public CarResponseDto createCar(CarCreateDto requestDto) {
        Car car = carMapper.toCar(requestDto);
        return carMapper.toCarResponseDto(carRepository.save(car));
    }

    @Override
    public Page<CarResponseDto> getCars(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toCarResponseDto);
    }

    @Override
    public List<CarResponseDto> search(CarSearchParameters searchParameters) {
        Specification<Car> specification = specificationBuilder.build(searchParameters);
        return carRepository.findAll(specification)
                .stream()
                .map(carMapper::toCarResponseDto)
                .toList();
    }

    @Override
    public CarResponseDto updateById(Long id, CarCreateDto carCreateDto) {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Car with id " + id + " not found"));
        carMapper.updateCarFromDto(carCreateDto,car);
        return carMapper.toCarResponseDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public CarResponseDto findById(Long id) {
        return carMapper.toCarResponseDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id " + id + " not found")));
    }
}
