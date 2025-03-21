package com.example.carsharingservice.service.car;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.dto.car.CarSearchParameters;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto createCar(CarCreateDto requestDto);

    Page<CarResponseDto> getCars(Pageable pageable);

    List<CarResponseDto> search(CarSearchParameters searchParameters);

    CarResponseDto updateById(Long id, @Valid CarCreateDto carCreateDto);

    void deleteById(Long id);

    CarResponseDto findById(Long id);
}
