package com.example.carsharingservice.util.rental;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalDataReturnRequestDto;
import java.time.LocalDate;

public class TestRentalUtil {
    public static RentalCreateDto getRentalCreateDto() {
        return new RentalCreateDto().setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7)).setCarId(1L);
    }

    public static RentalDataReturnRequestDto getRentalDataReturnRequestDto() {
        return new RentalDataReturnRequestDto(1L,LocalDate.now().plusDays(7));
    }
}
