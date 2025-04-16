package com.aman.booking.controller;

import com.aman.booking.entity.Load;
import com.aman.booking.service.LoadService;
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

@RequestMapping("/load")
public class LoadController {
    private static final Logger logger = LoggerFactory.getLogger(LoadController.class);

    @Autowired
    private LoadService loadService;


    @PostMapping
    public ResponseEntity<Load> createLoad(@Valid @RequestBody Load load) {
        logger.info("Received request to create load for shipperId: {}", load.getShipperId());
        Load createdLoad = loadService.createLoad(load);
        logger.info("Load created successfully with id: {}", createdLoad.getId());
        return new ResponseEntity<>(createdLoad, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Load> getLoads(@RequestParam(required = false) String shipperId,
                               @RequestParam(required = false) String truckType,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String loadingPoint,
                               @RequestParam(required = false) String unloadingPoint) {
        logger.info("Received request to get loads with filters - shipperId: {}, truckType: {}, status: {}, " +
                        "loadingPoint: {}, unloadingPoint: {}",
                shipperId, truckType, status, loadingPoint, unloadingPoint);
        return loadService.getFilteredLoads(shipperId, truckType, status, loadingPoint, unloadingPoint);
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<Load> getLoad(@PathVariable("loadId") UUID loadId) {
        logger.info("Received request to get load with id: {}", loadId);
        Load load = loadService.getLoad(loadId);
        return ResponseEntity.ok(load);
    }

    @PutMapping("/{loadId}")
    public ResponseEntity<Load> updateLoad(@PathVariable("loadId") UUID loadId,@Valid @RequestBody Load load) {
        logger.info("Received request to update load with id: {}", loadId);
        Load updatedLoad = loadService.updateLoad(loadId, load);
        logger.info("Load updated successfully: {}", loadId);
        return ResponseEntity.ok(updatedLoad);
    }

    @DeleteMapping("/{loadId}")
    public ResponseEntity<Void> deleteLoad(@PathVariable("loadId") UUID loadId) {
        logger.info("Received request to delete load with id: {}", loadId);
        loadService.deleteLoad(loadId);
        logger.info("Load deleted successfully: {}", loadId);
        return ResponseEntity.noContent().build();

    }
}
