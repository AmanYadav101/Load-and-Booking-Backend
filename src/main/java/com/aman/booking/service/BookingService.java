package com.aman.booking.service;

import com.aman.booking.entity.Booking;
import com.aman.booking.entity.Load;
import com.aman.booking.exception.BusinessRuleViolationException;
import com.aman.booking.exception.InvalidDataException;
import com.aman.booking.exception.ResourceNotFoundException;
import com.aman.booking.repository.BookingRepository;
import com.aman.booking.repository.LoadRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);


    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LoadRepository loadRepository;

    @Transactional
    public Booking createBooking(Booking booking) {
        logger.info("Creating new booking for transporterId: {}", booking.getTransporterId());

        Load load = booking.getLoad();
        if (load == null) {
            logger.error("Attempt to create booking without a load");
            throw new InvalidDataException("Booking must be associated with a load");
        }
        if ("CANCELED".equals(load.getStatus())) {
            logger.warn("Attempt to create booking for cancelled load: {}", load.getId());
            throw new BusinessRuleViolationException("Booking can't be created for a cancelled load");
        }
        if (booking.getStatus() != null && !Arrays.asList("PENDING", "ACCEPTED", "REJECTED").contains(booking.getStatus().toUpperCase())) {
            logger.error("Invalid booking status provided: {}", booking.getStatus());
            throw new InvalidDataException("Invalid booking status: " + booking.getStatus());
        }
        Booking savedBooking = bookingRepository.save(booking);
        logger.debug("Booking saved with ID: {}", savedBooking.getId());
        load.setStatus("BOOKED");
        loadRepository.save(load);
        logger.info("Load status updated to BOOKED for loadId: {}", load.getId());
        return savedBooking;
    }

    public Optional<Booking> getBooking(UUID bookingId) {
        logger.info("Fetching booking with id: {}", bookingId);
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking updateBooking(UUID bookingId, Booking bookingDetails) {
        logger.info("Updating booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found with id: {}", bookingId);
                    return new ResourceNotFoundException("Booking not found with id: " + bookingId);
                });
        booking.setTransporterId(bookingDetails.getTransporterId());
        booking.setProposedRate(bookingDetails.getProposedRate());
        booking.setComment(bookingDetails.getComment());

        String newStatus = bookingDetails.getStatus();
        if (newStatus != null) {
            switch (newStatus.toUpperCase()) {
                case "ACCEPTED":
                case "REJECTED":
                case "PENDING":
                    booking.setStatus(newStatus.toUpperCase());
                    logger.debug("Booking status updated to: {}", newStatus.toUpperCase());
                    break;
                default:
                    logger.error("Invalid status provided for update: {}", newStatus);
                    throw new InvalidDataException("Invalid status: " + newStatus);
            }
        }
        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(UUID bookingId) {
        logger.info("Booking updated successfully: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Cannot delete - booking not found with id: {}", bookingId);
                    return new ResourceNotFoundException("Booking not found with id: " + bookingId);
                });
        Load load = booking.getLoad();
        if (load != null) {
            load.setStatus("CANCELLED");
            loadRepository.save(load);
            logger.info("Load status updated to CANCELLED for loadId: {}", load.getId());
        }
        logger.info("Booking deleted successfully: {}", bookingId);
        bookingRepository.delete(booking);
    }

    public List<Booking> getFilteredBookings(String transporterId, String shipperId, String status) {
        logger.info("Fetching filtered bookings - transporterId: {}, shipperId: {}, status: {}", transporterId, shipperId, status);

        List<Booking> bookings = bookingRepository.findAll();

        return bookings.stream().filter(booking -> transporterId == null || booking.getTransporterId().equals(transporterId)).filter(booking -> status == null || booking.getStatus().equals(status)).filter(booking -> shipperId == null || (booking.getLoad() != null && booking.getLoad().getShipperId().equals(shipperId))).collect(Collectors.toList());
    }
}
