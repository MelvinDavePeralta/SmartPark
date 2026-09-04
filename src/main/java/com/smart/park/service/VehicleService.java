package com.smart.park.service;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.VehicleRequest;
import com.smart.park.entity.ParkingLot;
import com.smart.park.entity.Vehicle;

public interface VehicleService {

    public Vehicle registerVehicle(VehicleRequest vehicleRequest);

}