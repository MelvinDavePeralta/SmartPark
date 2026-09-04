package com.smart.park.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ParkedVehicleResponse {

    private String licensePlate;
    private String ownerName;
    private String type;
    private LocalDateTime checkInTime;
}