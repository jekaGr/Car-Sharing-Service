package com.example.carsharingservice.controller;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalDataReturnRequestDto;
import com.example.carsharingservice.dto.rental.RentalResponseDto;
import com.example.carsharingservice.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Managing users' car rentals", description = "Endpoints of management rentals.")
@RequestMapping("/rentals")
@RestController
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create car", description = "Create car")
    public RentalResponseDto createRental(@RequestBody @Valid RentalCreateDto requestDto) {
        return rentalService.createRental(requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    @Operation(summary = "Get rentals page",
            description = "Get rentals page by specific params."
                    + " Available for Manager roles")
    public Page<RentalResponseDto> getAll(@ParameterObject Pageable pageable,
                                          @RequestParam("userId") @NotNull
                                                  @Positive Long userId,
                                          @RequestParam("isActive") @NotNull
                                                  boolean isActive) {
        return rentalService.findAll(pageable, userId, isActive);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get rental by id",
            description = "Get rental by id")
    public RentalResponseDto getById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/return")
    @Operation(summary = "Complete rental",
            description = "Complete rental."
                    + " Available for Costumer and Manager roles")
    public RentalResponseDto completeRental(
            @RequestBody @Valid RentalDataReturnRequestDto requestDto) {
        return rentalService.setDataReturn(requestDto);
    }
}
