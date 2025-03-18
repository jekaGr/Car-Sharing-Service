package com.example.carsharingservice.mapper;

import com.example.carsharingservice.config.MapperConfig;
import com.example.carsharingservice.dto.payment.PaymentResponseDto;
import com.example.carsharingservice.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentResponseDto toDto(Payment payment);

    default Page<PaymentResponseDto> toPaymentPage(Page<Payment> allByRentalUserId) {
        List<PaymentResponseDto> dtoList = allByRentalUserId.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, allByRentalUserId.getPageable(),
                allByRentalUserId.getTotalElements());
    }
}
