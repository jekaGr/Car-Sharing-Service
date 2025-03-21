package com.example.carsharingservice.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record RentalDataReturnRequestDto(
        @NotNull @Positive Long rentalId,
        @NotNull @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate returnDate) {}
