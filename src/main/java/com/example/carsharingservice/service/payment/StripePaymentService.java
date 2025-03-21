package com.example.carsharingservice.service.payment;

import com.example.carsharingservice.exception.PaymentException;
import com.example.carsharingservice.model.Payment;
import com.example.carsharingservice.model.Rental;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private static final String SUCCESSFUL_PAYMENT_PATH = "/payments/success/";
    private static final String CANCELED_PAYMENT_PATH = "/payments/cancel/";
    private static final String CURRENCY = "usd";

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${payment.callback.domain:https://localhost:8081}")
    private String domain;

    public Session createStripeSession(Payment payment, Rental rental, BigDecimal amountToPay) {
        Stripe.apiKey = stripeSecretKey;

        final long expirationTime =
                Instant.now().plusSeconds(24 * 60 * 59).getEpochSecond();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + SUCCESSFUL_PAYMENT_PATH + payment.getId())
                .setCancelUrl(domain + CANCELED_PAYMENT_PATH + payment.getId())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem
                                                                .PriceData
                                                                .ProductData
                                                                .builder()
                                                                .setName("Payment for rental "
                                                                        + rental.getId())
                                                                .build())
                                                .setUnitAmount(
                                                        amountToPay
                                                                .multiply(
                                                                        BigDecimal
                                                                                .valueOf(100))
                                                                .longValue())
                                                .setCurrency(CURRENCY)
                                                .build())
                                .build())
                .setExpiresAt(expirationTime)
                .build();
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new PaymentException("Can't create session");
        }
        return session;
    }

    public void setPaymentSessionUrl(Payment payment, Session session) {
        try {
            payment.setSessionUrl(URI.create(session.getUrl()).toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + session.getUrl(), e);
        }
    }
}
