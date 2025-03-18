package com.example.carsharingservice.mapper;

import com.example.carsharingservice.config.MapperConfig;
import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalResponseDto;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.model.Rental;
import com.example.carsharingservice.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {

    @Mapping(source = "rental.actualReturnDate", target = "actualReturnDate")
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toRentalResponseDto(Rental rental);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    Rental toModelByDtoAndCarAndUser(RentalCreateDto requestDto,Car car,User user);

    default Page<RentalResponseDto> toDtoPage(Page<Rental> rentalPage) {
        List<RentalResponseDto> dtoList = rentalPage.getContent().stream()
                .map(this::toRentalResponseDto)
                .toList();
        return new PageImpl<>(dtoList, rentalPage.getPageable(), rentalPage.getTotalElements());
    }
}
