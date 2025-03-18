package com.example.carsharingservice.service.rental;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
import com.example.carsharingservice.dto.rental.RentalDataReturnRequestDto;
import com.example.carsharingservice.dto.rental.RentalResponseDto;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.exception.NoAvailableCarsException;
import com.example.carsharingservice.mapper.RentalMapper;
import com.example.carsharingservice.model.Car;
import com.example.carsharingservice.model.Rental;
import com.example.carsharingservice.model.User;
import com.example.carsharingservice.repository.CarRepository;
import com.example.carsharingservice.repository.RentalRepository;
import com.example.carsharingservice.service.telegram.NotificationService;
import com.example.carsharingservice.service.user.UserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public RentalResponseDto createRental(RentalCreateDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Car not found"));
        if (car.getInventory() < 1) {
            throw new NoAvailableCarsException("Error when processing rental: car with id ["
                    + car.getId() + "] No available cars");
        }

        car.setInventory(car.getInventory() - 1);
        User user = userService.getMe();
        Rental rental = rentalMapper.toModelByDtoAndCarAndUser(requestDto,car,user);

        carRepository.save(car);
        rentalRepository.save(rental);
        notificationService.sendNotification("you created a new car rental");
        return rentalMapper.toRentalResponseDto(rental);
    }

    @Override
    public RentalResponseDto getById(Long id) {
        return rentalMapper.toRentalResponseDto(rentalRepository.getById(id));
    }

    @Override
    public Page<RentalResponseDto> findAll(Pageable pageable, Long userId, boolean isActive) {
        Page<Rental> rentalsPage;
        if (isActive) {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNull(pageable, userId);
        } else {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNotNull(pageable, userId);
        }
        return rentalMapper.toDtoPage(rentalsPage);
    }

    @Override
    public RentalResponseDto setDataReturn(RentalDataReturnRequestDto requestDto) {
        Rental rental = rentalRepository.findById(requestDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException("Rental not found with id")
        );

        Car car = carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException("Car not found with id")
        );
        LocalDate returnDate = requestDto.returnDate();
        rental.setActualReturnDate(returnDate);
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rentalRepository.save(rental);
        notificationService.sendNotification("return date changed to  "
                + returnDate);
        return rentalMapper.toRentalResponseDto(rental);
    }
}
