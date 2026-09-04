package com.smart.park.service;

import com.smart.park.dto.ParkedVehicleResponse;
import com.smart.park.dto.ParkingLotAvailabilityResponse;
import com.smart.park.dto.ParkingSessionRequest;
import com.smart.park.entity.ParkingSession;

import java.util.List;

public interface ParkingSessionService {

    public ParkingSession checkIn(ParkingSessionRequest parkingSessionRequest);

    public ParkingSession checkOut(ParkingSessionRequest parkingSessionRequest);

    public ParkingLotAvailabilityResponse getParkingLotAvailability(String lotId);

    public List<ParkedVehicleResponse> getCurrentParkedVehicles(String lotId);
}
