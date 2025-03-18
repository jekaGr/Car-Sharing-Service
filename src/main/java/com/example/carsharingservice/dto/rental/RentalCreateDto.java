package com.example.carsharingservice.dto.rental;

import com.example.carsharingservice.validation.ValidDateFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RentalCreateDto {
    @NotNull
    @PastOrPresent
    @ValidDateFormat
    private LocalDate rentalDate;

    @NotNull
    @FutureOrPresent
    @ValidDateFormat
    private LocalDate returnDate;

    @NotNull
    @Positive
    private Long carId;
}
