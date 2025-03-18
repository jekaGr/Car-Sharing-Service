package com.example.carsharingservice.util.car;

import com.example.carsharingservice.dto.car.CarCreateDto;
import com.example.carsharingservice.dto.car.CarResponseDto;
import com.example.carsharingservice.model.Car;
import java.math.BigDecimal;

public class TestCarUtil {
    public static Car createCar() {
        return new Car().setId(1L).setModel("X6").setBrand("BMW").setType(Car.Type.SEDAN)
                .setInventory(5).setDailyFee(new BigDecimal("100"));

    }

    public static Car createForSavingCar() {
        return new Car().setModel("X6").setBrand("BMW").setType(Car.Type.SEDAN)
                .setInventory(5).setDailyFee(new BigDecimal("100"));

    }

    public static CarResponseDto getCarResponseDto() {
        return new CarResponseDto().setId(1L).setModel("X6").setBrand("BMW")
                .setDailyFee(new BigDecimal("100"))
                .setType(Car.Type.SEDAN).setInventory(5);
    }

    public static CarCreateDto getCarCreateDto() {
        return new CarCreateDto().setModel("X6").setBrand("BMW").setType(Car.Type.SEDAN)
                .setDailyFee(new BigDecimal("100")).setInventory(5);
    }

    public static CarCreateDto getUpdateCar() {
        return new CarCreateDto().setModel("X5").setBrand("BMW").setType(Car.Type.SUV)
                .setInventory(7).setDailyFee(new BigDecimal("29.99"));
    }
}
