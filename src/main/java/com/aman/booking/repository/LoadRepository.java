package com.aman.booking.repository;

import com.aman.booking.entity.Load;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoadRepository extends JpaRepository<Load, UUID> {
    List<Load> findByShipperId(String shipperId);
    List<Load> findByTruckType(String truckType);
    List<Load> findByStatus(String status);
    List<Load> findByShipperIdAndTruckType(String shipperId, String truckType);
    List<Load> findByShipperIdAndStatus(String shipperId, String status);
    List<Load> findByTruckTypeAndStatus(String truckType, String status);
    List<Load> findByShipperIdAndTruckTypeAndStatus(String shipperId, String truckType, String status);

}
