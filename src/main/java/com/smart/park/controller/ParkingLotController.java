package com.smart.park.controller;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.ParkingLotResponse;
import com.smart.park.entity.ParkingLot;
import com.smart.park.service.ParkingLotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parking-lot")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping("/register")
    public ResponseEntity<ParkingLotResponse> registerLot(@Valid @RequestBody ParkingLotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingLotService.registerParkingLot(request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ParkingLotResponse>> getAllLots() {
        return ResponseEntity.ok(parkingLotService.showAllParkingLot());
    }
}