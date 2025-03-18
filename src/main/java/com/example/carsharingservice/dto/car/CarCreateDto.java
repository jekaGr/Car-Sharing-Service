package com.example.carsharingservice.dto.car;

import com.example.carsharingservice.model.Car;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CarCreateDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dailyFee;
    @NotNull
    private Car.Type type;
    @NotNull
    @Min(0)
    private int inventory;
}
