package com.smart.park.service.impl;

import com.smart.park.dto.VehicleRequest;
import com.smart.park.entity.Vehicle;
import com.smart.park.exception.BusinessException;
import com.smart.park.repository.VehicleRepository;
import com.smart.park.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public Vehicle registerVehicle(VehicleRequest vehicleRequest) {
        log.info("Start register vehicle : {}", vehicleRequest.getLicensePlate());
        if (vehicleRepository.existsById(vehicleRequest.getLicensePlate())) {
            throw new BusinessException("Vehicle already exists");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(vehicleRequest.getLicensePlate());
        vehicle.setType(vehicleRequest.getType());
        vehicle.setOwnerName(vehicleRequest.getOwnerName());

        log.info("Vehicle  has been saved : {}", vehicle.getLicensePlate());
        return vehicleRepository.save(vehicle);
    }
}
