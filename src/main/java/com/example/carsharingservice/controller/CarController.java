package com.example.carsharingservice.controller;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.dto.car.CarSearchParameters;
import com.example.carsharingservice.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints of management cars.")
@RequestMapping("/cars")
@RestController
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create car", description = "Create car")
    public CarResponseDto createCar(@RequestBody @Valid CarCreateDto requestDto) {
        return carService.createCar(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all car", description = "Get all car")
    public Page<CarResponseDto> getAllCar(@ParameterObject @PageableDefault Pageable pageable) {
        return carService.getCars(pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/search")
    @Operation(summary = "Search cars",
            description = "Search for cars based on specific parameters")
    public List<CarResponseDto> searchCars(CarSearchParameters searchParameters) {
        return carService.search(searchParameters);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete car",
            description = "Remove car by ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update car by ID",
            description = "Update car by ID")
    public CarResponseDto update(@PathVariable Long id,
                          @RequestBody @Valid CarCreateDto carCreateDto) {
        return carService.updateById(id, carCreateDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get car by ID",
            description = "Get details of a car by ID")
    public CarResponseDto getBookById(@PathVariable Long id) {
        return carService.findById(id);
    }
}
