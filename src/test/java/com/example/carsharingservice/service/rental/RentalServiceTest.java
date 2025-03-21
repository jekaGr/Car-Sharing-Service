package com.example.carsharingservice.service.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingservice.dto.rental.RentalCreateDto;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Car car;
    private Rental rental;
    private User user;
    private RentalCreateDto createDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setInventory(5);

        rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);

        user = new User();
        user.setId(1L);

        createDto = new RentalCreateDto().setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7)).setCarId(1L);
    }

    @Test
    void createRental_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userService.getMe()).thenReturn(user);
        when(rentalMapper.toModelByDtoAndCarAndUser(any(), any(), any())).thenReturn(rental);
        when(rentalRepository.save(any())).thenReturn(rental);
        when(rentalMapper.toRentalResponseDto(any())).thenReturn(new RentalResponseDto());

        RentalResponseDto responseDto = rentalService.createRental(createDto);

        assertEquals(new RentalResponseDto(), responseDto);
        assertEquals(4, car.getInventory());
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
        verify(notificationService).sendNotification("you created a new car rental");
    }

    @Test
    void createRental_carNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rentalService.createRental(createDto));
    }

    @Test
    void createRental_noAvailableCars() {
        car.setInventory(0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(NoAvailableCarsException.class, () -> rentalService.createRental(createDto));
    }

    @Test
    void getById_success() {
        when(rentalRepository.getById(1L)).thenReturn(rental);
        when(rentalMapper.toRentalResponseDto(rental)).thenReturn(new RentalResponseDto());

        RentalResponseDto responseDto = rentalService.getById(1L);

        assertEquals(new RentalResponseDto(), responseDto);
    }

    @Test
    void findAll_isActive_success() {
        Pageable pageable = Pageable.unpaged();
        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNull(pageable,
                1L)).thenReturn(new PageImpl<>(java.util.List.of(rental)));
        when(rentalMapper.toRentalResponseDto(any())).thenReturn(new RentalResponseDto());

        Page<RentalResponseDto> responseDtoPage = rentalService.findAll(pageable, 1L, true);

        assertEquals(1, responseDtoPage.getContent().size());
    }

    @Test
    void findAll_isNotActive_success() {
        Pageable pageable = Pageable.unpaged();
        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNotNull(pageable,
                1L)).thenReturn(new PageImpl<>(java.util.List.of(rental)));
        when(rentalMapper.toRentalResponseDto(any())).thenReturn(new RentalResponseDto());

        Page<RentalResponseDto> responseDtoPage = rentalService.findAll(pageable,
                1L, false);

        assertEquals(1, responseDtoPage.getContent().size());
    }
}
