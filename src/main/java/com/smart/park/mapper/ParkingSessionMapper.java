package com.smart.park.mapper;

import com.smart.park.dto.ParkedVehicleResponse;
import com.smart.park.dto.ParkingLotAvailabilityResponse;
import com.smart.park.entity.ParkingLot;
import com.smart.park.entity.ParkingSession;
import org.springframework.stereotype.Component;

@Component
public class ParkingSessionMapper {

    public ParkingLotAvailabilityResponse toAvailabilityResponse(ParkingLot parkingLot) {
        int availableSpaces = parkingLot.getCapacity() - parkingLot.getOccupiedSpaces();
        return new ParkingLotAvailabilityResponse(
                parkingLot.getLotId(),
                parkingLot.getLocation(),
                parkingLot.getCapacity(),
                parkingLot.getOccupiedSpaces(),
                availableSpaces
        );
    }

    public ParkedVehicleResponse toParkedVehicleResponse(ParkingSession session) {
        return new ParkedVehicleResponse(
                session.getVehicle().getLicensePlate(),
                session.getVehicle().getOwnerName(),
                session.getVehicle().getType().name(),
                session.getCheckInTime()
        );
    }
}