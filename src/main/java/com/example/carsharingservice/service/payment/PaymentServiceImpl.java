package com.example.carsharingservice.service.payment;

import com.example.carsharingservice.dto.payment.PaymentRequestDto;
import com.example.carsharingservice.dto.payment.PaymentResponseDto;
import com.example.carsharingservice.exception.EntityNotFoundException;
import com.example.carsharingservice.exception.PaymentException;
import com.example.carsharingservice.mapper.PaymentMapper;
import com.example.carsharingservice.model.Payment;
import com.example.carsharingservice.model.Rental;
import com.example.carsharingservice.model.User;
import com.example.carsharingservice.repository.PaymentRepository;
import com.example.carsharingservice.repository.RentalRepository;
import com.example.carsharingservice.service.telegram.NotificationService;
import com.example.carsharingservice.service.user.UserService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = new BigDecimal("1.5");

    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final StripePaymentService stripePaymentService;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        Payment payment = getCreatedPayment(requestDto);

        Session session = stripePaymentService.createStripeSession(payment,
                payment.getRental(), payment.getAmountToPay());

        payment.setType(isRentalClosedLate(payment.getRental())
                ? Payment.PaymentType.FINE : Payment.PaymentType.PAYMENT);
        payment.setSessionId(session.getId());
        stripePaymentService.setPaymentSessionUrl(payment, session);
        paymentRepository.save(payment);
        notificationService.sendNotification("payment made successfully");
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentResponseDto> getAllPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRental_UserId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional
    public void checkSuccessfulPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        paymentRepository.save(payment);
        notificationService.sendNotification(
                createNotificationForSuccessPayment(payment));
    }

    private Payment getCreatedPayment(PaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rental with id ["
                                + requestDto.getRentalId() + "] "));

        Payment payment;

        if (paymentRepository.existsByRentalId(rental.getId())) {
            payment = paymentRepository.findByRentalId(rental.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find payment by rental id ["
                                    + requestDto.getRentalId() + "] "));
        } else {
            payment = new Payment();
        }

        checkIfUserIsRentalOwner(rental);
        checkIfRentalIsAlreadyClosed(rental);
        checkIfPaymentIsAlreadyDone(rental);

        BigDecimal amountToPay = calculateAmountToPay(rental);
        payment.setAmountToPay(amountToPay);
        payment.setRental(rental);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setType(Payment.PaymentType.PAYMENT);
        payment.setSessionId("none");
        try {
            payment.setSessionUrl(URI.create("http://none.none").toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    "Error when set default url for payment with rental id ["
                            + rental.getId() + "]", e);
        }
        return paymentRepository.save(payment);
    }

    private void checkIfUserIsRentalOwner(Rental rental) {
        User user = userService.getMe();
        if (!rental.getUser().getId().equals(user.getId())) {
            throw new PaymentException(
                    "Sorry, but you can't pay for other users' rental fees");
        }
    }

    private void checkIfRentalIsAlreadyClosed(Rental rental) {
        if (rental.getActualReturnDate() == null) {
            throw new PaymentException("This rental hasn't been closed. "
                    + "You should close rental before payment");
        }
    }

    private void checkIfPaymentIsAlreadyDone(Rental rental) {
        Optional<Payment> optionalPaymentForTheRental =
                paymentRepository.findByRentalId(rental.getId())
                        .filter(payment -> payment.getStatus().equals(Payment.PaymentStatus.PAID));

        if (optionalPaymentForTheRental.isPresent()) {
            throw new PaymentException(
                    "Rental with id " + rental.getId() + " was already paid.");
        }
    }

    private BigDecimal calculateAmountToPay(Rental rental) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        if (isRentalClosedLate(rental)) {
            long claimedRentalDuration = Math.max(1,
                    rental.getReturnDate().toEpochDay()
                            - rental.getRentalDate().toEpochDay());
            long overdueRentalDuration = Math.max(1,
                    rental.getActualReturnDate().toEpochDay()
                            - rental.getReturnDate().toEpochDay());

            BigDecimal claimedRentalDurationFee =
                    dailyFee.multiply(BigDecimal.valueOf(claimedRentalDuration));
            BigDecimal overdueRentalDurationFee =
                    dailyFee.multiply(FINE_MULTIPLIER)
                            .multiply(BigDecimal.valueOf(overdueRentalDuration));

            return claimedRentalDurationFee.add(overdueRentalDurationFee);
        } else {
            long rentalDuration = Math.max(1,
                    rental.getActualReturnDate().toEpochDay()
                            - rental.getRentalDate().toEpochDay());
            return dailyFee.multiply(BigDecimal.valueOf(rentalDuration));
        }
    }

    private static boolean isRentalClosedLate(Rental rental) {
        return rental.getActualReturnDate().isAfter(rental.getReturnDate());
    }

    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Payment with id " + id + " not found"));
    }

    private String createNotificationForSuccessPayment(Payment payment) {
        return "success payment received by user with email ["
                + payment.getRental().getUser().getEmail()
                + "], amount ["
                + payment.getAmountToPay()
                + "]";
    }
}
