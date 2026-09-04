package com.smart.park.service;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.ParkingLotResponse;
import com.smart.park.entity.ParkingLot;

import java.util.List;

public interface ParkingLotService {

    public ParkingLotResponse registerParkingLot(ParkingLotRequest parkingLotRequest);

    public List<ParkingLotResponse> showAllParkingLot();

}