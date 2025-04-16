package com.aman.booking.controller;

import com.aman.booking.dto.BookingRequest;
import com.aman.booking.entity.Booking;
import com.aman.booking.entity.Load;
import com.aman.booking.exception.ResourceNotFoundException;
import com.aman.booking.repository.LoadRepository;
import com.aman.booking.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private LoadRepository loadRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        logger.info("Received request to create booking for loadId: {}", bookingRequest.getLoadId());

        Load load = loadRepository.findById(bookingRequest.getLoadId()).orElseThrow(() -> {
            logger.error("Load not found with id: {}", bookingRequest.getLoadId());
            return new ResourceNotFoundException("Load not found with id: " + bookingRequest.getLoadId());
        });
        Booking booking = new Booking();
        booking.setLoad(load);
        booking.setTransporterId(bookingRequest.getTransporterId());
        booking.setProposedRate(bookingRequest.getProposedRate());
        booking.setComment(bookingRequest.getComment());
        booking.setStatus(bookingRequest.getStatus());
        booking.setRequestedAt(bookingRequest.getRequestedAt());

        Booking savedBooking = bookingService.createBooking(booking);
        logger.info("Booking created successfully with id: {}", savedBooking.getId());
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);

    }

    @GetMapping
    public List<Booking> getBookings(@RequestParam(required = false) String transporterId, @RequestParam(required = false) String shipperId, @RequestParam(required = false) String status) {
        logger.info("Received request to get bookings with filters - transporterId: {}, shipperId: {}, status: {}", transporterId, shipperId, status);
        return bookingService.getFilteredBookings(transporterId, shipperId, status);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {
        logger.info("Received request to get booking with id: {}", bookingId);
        Booking booking = bookingService.getBooking(bookingId).orElseThrow(() -> {
            logger.error("Booking not found with id: {}", bookingId);
            return new ResourceNotFoundException("Booking not found with id: " + bookingId);
        });
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID bookingId, @Valid @RequestBody Booking booking) {
        logger.info("Received request to update booking with id: {}", bookingId);

        Booking updatedBooking = bookingService.updateBooking(bookingId, booking);
        logger.info("Booking updated successfully: {}", bookingId);
        return ResponseEntity.ok(updatedBooking);

    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        logger.info("Received request to delete booking with id: {}", bookingId);
        bookingService.deleteBooking(bookingId);
        logger.info("Booking deleted successfully: {}", bookingId);
        return ResponseEntity.noContent().build();
    }

}

