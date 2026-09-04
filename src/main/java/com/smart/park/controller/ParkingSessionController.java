package com.smart.park.controller;

import com.smart.park.dto.ParkedVehicleResponse;
import com.smart.park.dto.ParkingLotAvailabilityResponse;
import com.smart.park.dto.ParkingSessionRequest;
import com.smart.park.entity.ParkingSession;
import com.smart.park.service.ParkingSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parking")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ParkingSession> checkIn(@Valid @RequestBody ParkingSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSessionService.checkIn(request));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ParkingSession> checkOut(@Valid @RequestBody ParkingSessionRequest request) {
        return ResponseEntity.ok(parkingSessionService.checkOut(request));
    }

    @GetMapping("/lots/{lotId}/availability")
    public ResponseEntity<ParkingLotAvailabilityResponse> getAvailability(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingSessionService.getParkingLotAvailability(lotId));
    }

    @GetMapping("/lots/{lotId}/vehicles")
    public ResponseEntity<List<ParkedVehicleResponse>> getParkedVehicles(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingSessionService.getCurrentParkedVehicles(lotId));
    }
}