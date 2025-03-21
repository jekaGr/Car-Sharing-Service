package com.example.carsharingservice.service.car;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.mapper.CarMapper;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.repository.CarRepository;
import com.example.carsharingservice.util.car.TestCarUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    private Car car;
    private CarResponseDto carResponseDto;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        car = TestCarUtil.createCar();
        carResponseDto = TestCarUtil.getCarResponseDto();
    }

    @Test
    void createCar_should_return_CarResponseDto() {
        CarCreateDto carCreateDto = TestCarUtil.getCarCreateDto();

        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCar(carCreateDto)).thenReturn(car);
        when(carMapper.toCarResponseDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.createCar(carCreateDto);

        assertNotNull(result);
        assertEquals(TestCarUtil.getCarResponseDto().getId(), result.getId());
        assertEquals(TestCarUtil.getCarResponseDto().getModel(), result.getModel());
    }

    @Test
    void getCars_should_return_P() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<Car> cars = List.of(car);
        Page<Car> carsPage = new PageImpl<>(cars);
        when(carRepository.findAll(pageable)).thenReturn(carsPage);
        when(carMapper.toCarResponseDto(car)).thenReturn(carResponseDto);

        // When
        Page<CarResponseDto> actual = carService.getCars(pageable);

        // Then
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertEquals(carResponseDto, actual.getContent().get(0));
    }

    @Test
    void updateCar_should_return_CarResponseDto() {
        // Given;
        CarResponseDto expectedResponse = carResponseDto;
        CarCreateDto updateCarDto = TestCarUtil.getUpdateCar();
        when(carRepository.findById(any())).thenReturn(Optional.of(car));
        doNothing().when(carMapper).updateCarFromDto(updateCarDto, car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarResponseDto(car)).thenReturn(expectedResponse);

        // When
        CarResponseDto actualResponse = carService.updateById(1L, updateCarDto);

        assertEquals(expectedResponse, actualResponse);
        verify(carRepository).findById(1L);
        verify(carMapper).updateCarFromDto(updateCarDto, car);
        verify(carRepository).save(car);
        verify(carMapper).toCarResponseDto(car);
    }

    @Test
    void getCarById_should_return_CarResponseDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toCarResponseDto(car)).thenReturn(carResponseDto);
        CarResponseDto result = carService.findById(1L);
        assertNotNull(result);
        assertEquals(TestCarUtil.getCarResponseDto().getId(), result.getId());
        assertEquals(TestCarUtil.getCarResponseDto().getModel(), result.getModel());
    }

    @Test
    void deleteCarById_should_call_repository_deleteById() {
        // Given
        Long carId = 1L;

        // When
        carService.deleteById(carId);

        // Then
        verify(carRepository).deleteById(carId);
    }
}
