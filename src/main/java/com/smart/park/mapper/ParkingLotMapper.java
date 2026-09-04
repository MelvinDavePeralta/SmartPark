package com.smart.park.mapper;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.ParkingLotResponse;
import com.smart.park.entity.ParkingLot;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapper {

    public ParkingLot toEntity(ParkingLotRequest request) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setLotId(request.getLotId());
        parkingLot.setLocation(request.getLocation());
        parkingLot.setCapacity(request.getCapacity());
        parkingLot.setCostPerMinute(request.getCostPerMinute());
        parkingLot.setOccupiedSpaces(0);

        return parkingLot;
    }

    public ParkingLotResponse toResponse(ParkingLot parkingLot) {
        ParkingLotResponse response = new ParkingLotResponse();
        response.setLotId(parkingLot.getLotId());
        response.setLocation(parkingLot.getLocation());
        response.setCapacity(parkingLot.getCapacity());
        response.setOccupiedSpaces(parkingLot.getOccupiedSpaces());
        response.setAvailableSpaces(
                parkingLot.getCapacity() - parkingLot.getOccupiedSpaces()
        );
        response.setCostPerMinute(parkingLot.getCostPerMinute());

        return response;
    }
}