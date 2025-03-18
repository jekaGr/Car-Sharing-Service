package com.example.carsharingservice.service.rental;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalDataReturnRequestDto;
import com.example.carsharingservice.dto.rental.RentalResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto createRental(RentalCreateDto requestDto);

    RentalResponseDto getById(Long id);

    Page<RentalResponseDto> findAll(Pageable pageable, Long userId, boolean isActive);

    RentalResponseDto setDataReturn(RentalDataReturnRequestDto requestDto);
}
