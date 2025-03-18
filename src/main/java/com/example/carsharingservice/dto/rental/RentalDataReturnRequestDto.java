package com.example.carsharingservice.dto.rental;

import com.example.carsharingservice.validation.ValidDateFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record RentalDataReturnRequestDto(@NotNull @Positive Long rentalId,
                                         @NotNull @FutureOrPresent @ValidDateFormat
                                         LocalDate returnDate) {}
