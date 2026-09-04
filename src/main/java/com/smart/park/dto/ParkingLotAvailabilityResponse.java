package com.smart.park.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParkingLotAvailabilityResponse {

    private String lotId;
    private String location;
    private Integer capacity;
    private Integer occupiedSpaces;
    private Integer availableSpaces;
}