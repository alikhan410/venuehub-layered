package com.venuehub.controller.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.venuehub.dto.BookingIdDto;
import com.venuehub.entity.Booking;
import com.venuehub.entity.BookingOrder;
import com.venuehub.enums.BookingStatus;
import com.venuehub.enums.OrderStatus;
import com.venuehub.exceptions.*;
import com.venuehub.response.BookingOrderListResponse;
import com.venuehub.response.ConfirmPaymentResponse;
import com.venuehub.response.CreatePaymentResponse;
import com.venuehub.service.BookingService;
import com.venuehub.service.OrderService;
import com.venuehub.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class PaymentController implements PaymentApi {
    public static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService, OrderService orderService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.orderService = orderService;
    }

    @PostMapping("/orders/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@RequestBody BookingIdDto bookingIdDto, @AuthenticationPrincipal Jwt jwt) throws StripeException {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }

        Booking booking = bookingService.findById(bookingIdDto.bookingId());

        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new ActionForbiddenException();
        //TODO redo these exceptions
        if (!jwt.getSubject().equals(booking.getReservedBy())) throw new UserForbiddenException();

        PaymentIntent paymentIntent = paymentService.createPayment(booking.getBookingFee());

        BookingOrder bookingOrder = new BookingOrder(
                jwt.getSubject(),
                booking.getVenue().getUsername(),
                paymentIntent.getClientSecret(),
                booking.getBookingFee(),
                booking.getId()
        );

        orderService.save(bookingOrder);

        CreatePaymentResponse response = new CreatePaymentResponse(bookingOrder.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/orders/status/{orderId}")
    public ResponseEntity<BookingOrder> getOrderStatus(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {
        BookingOrder order = orderService.findById(orderId).orElseThrow(NoSuchOrderException::new);
        Booking booking = bookingService.findById(order.getBookingId());
        if (!booking.getStatus().equals(BookingStatus.RESERVED))
            throw new ActionForbiddenException("Reservation expired");
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/orders/user")
    public ResponseEntity<BookingOrderListResponse> getUserOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }
        List<BookingOrder> order = orderService.findByUsername(jwt.getSubject());

        BookingOrderListResponse res = new BookingOrderListResponse(order);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/orders/vendor")
    public ResponseEntity<BookingOrderListResponse> getVendorOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }
        List<BookingOrder> order = orderService.findByServiceProvider(jwt.getSubject());
        BookingOrderListResponse res = new BookingOrderListResponse(order);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/orders/confirm-payment")
    @Transactional
    public ConfirmPaymentResponse confirmPayment(@RequestParam("clientId") String clientId, @RequestParam("clientSecret") String clientSecret, @RequestParam("vendor") String vendor, @AuthenticationPrincipal Jwt jwt) throws Exception {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }
        logger.info("clientId:{}, clientSecret:{}, vendor:{}", clientId, clientSecret, vendor);
        BookingOrder order = orderService.findByClientSecret(clientSecret).orElseThrow(NoSuchOrderException::new);

        if (order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new InvalidOrderStatusException("Order is already completed");
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(clientId);
        if (!paymentIntent.getStatus().equals("succeeded")) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setServiceProvider(vendor);
            orderService.save(order);

            throw new Exception("Payment did not succeed");
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setServiceProvider(vendor);
        orderService.save(order);
        bookingService.updateStatus(order.getBookingId(), BookingStatus.BOOKED);

        return new ConfirmPaymentResponse("Succeeded");
    }
}
