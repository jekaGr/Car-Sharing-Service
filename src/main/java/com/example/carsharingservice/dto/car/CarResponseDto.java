package com.example.carsharingservice.dto.car;

import com.example.carsharingservice.model.Car;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@EqualsAndHashCode
@Accessors(chain = true)
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private BigDecimal dailyFee;
    private Car.Type type;
    private int inventory;
}
