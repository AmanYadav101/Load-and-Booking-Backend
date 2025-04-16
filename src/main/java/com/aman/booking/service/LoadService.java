package com.aman.booking.service;

import com.aman.booking.entity.Booking;
import com.aman.booking.entity.Load;
import com.aman.booking.exception.BusinessRuleViolationException;
import com.aman.booking.exception.ResourceNotFoundException;
import com.aman.booking.repository.BookingRepository;
import com.aman.booking.repository.LoadRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoadService {
    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);

    @Autowired
    private LoadRepository loadRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Load createLoad(Load load) {
        logger.info("Creating new load for shipperId: {}", load.getShipperId());
        load.setStatus("POSTED");
        return loadRepository.save(load);
    }

    public Load getLoad(UUID loadId) {
        logger.info("Fetching load with id: {}", loadId);
        return loadRepository.findById(loadId).orElseThrow(() -> {
            logger.error("Load not found with id: {}", loadId);
            return new ResourceNotFoundException("Load not found with id: " + loadId);
        });
    }

    public List<Load> getAllLoads() {
        logger.info("Fetching all loads");
        return loadRepository.findAll();
    }

    @Transactional
    public Load updateLoad(UUID loadId, Load loadDetails) {
        logger.info("Updating load with id: {}", loadId);
        Load load = loadRepository.findById(loadId).orElseThrow(() -> {
            logger.error("Load not found with id: {}", loadId);
            return new ResourceNotFoundException("Load not found with id: " + loadId);
        });
        load.setShipperId(loadDetails.getShipperId());
        load.setFacility(loadDetails.getFacility());
        load.setProductType(loadDetails.getProductType());
        load.setTruckType(loadDetails.getTruckType());
        load.setNoOfTrucks(loadDetails.getNoOfTrucks());
        load.setWeight(loadDetails.getWeight());
        load.setComment(loadDetails.getComment());
        load.setDatePosted(loadDetails.getDatePosted());
        if (loadDetails.getStatus() != null) {
            load.setStatus(loadDetails.getStatus());
            logger.debug("Load status updated to: {}", loadDetails.getStatus());
        }
        Load updatedLoad = loadRepository.save(load);
        logger.info("Load updated successfully: {}", loadId);
        return updatedLoad;
    }

    @Transactional
    public void deleteLoad(UUID loadId) throws BusinessRuleViolationException {
        logger.info("Attempting to delete load with id: {}", loadId);
        Load load = loadRepository.findById(loadId).orElseThrow(() -> {
            logger.error("Cannot delete - load not found with id: {}", loadId);
            return new ResourceNotFoundException("Load not found with id: " + loadId);
        });
        List<Booking> bookings = bookingRepository.findByLoadId(loadId);
        if (!bookings.isEmpty()) {
            logger.warn("Cannot delete load {} - has {} active bookings", loadId, bookings.size());
            throw new BusinessRuleViolationException("Cannot delete load with active bookings");
        }
        loadRepository.delete(load);
        logger.info("Load deleted successfully: {}", loadId);
    }

    public List<Load> getFilteredLoads(String shipperId, String truckType, String status, String loadingPoint, String unloadingPoint) {
        logger.info("Fetching filtered loads - shipperId: {}, truckType: {}, status: {}, " + "loadingPoint: {}, unloadingPoint: {}", shipperId, truckType, status, loadingPoint, unloadingPoint);
        if (shipperId != null && truckType != null && status != null) {
            return loadRepository.findByShipperIdAndTruckTypeAndStatus(shipperId, truckType, status);
        } else if (shipperId != null && truckType != null) {
            return loadRepository.findByShipperIdAndTruckType(shipperId, truckType);
        } else if (shipperId != null && status != null) {
            return loadRepository.findByShipperIdAndStatus(shipperId, status);
        } else if (truckType != null && status != null) {
            return loadRepository.findByTruckTypeAndStatus(truckType, status);
        } else if (shipperId != null) {
            return loadRepository.findByShipperId(shipperId);
        } else if (truckType != null) {
            return loadRepository.findByTruckType(truckType);
        } else if (status != null) {
            return loadRepository.findByStatus(status);
        }

        List<Load> loads = loadRepository.findAll();
        return loads.stream().filter(load -> loadingPoint == null || (load.getFacility() != null && load.getFacility().getLoadingPoint().equals(loadingPoint))).filter(load -> unloadingPoint == null || (load.getFacility() != null && load.getFacility().getUnloadingPoint().equals(unloadingPoint))).collect(Collectors.toList());
    }


}
