package com.smart.park.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ParkingLotResponse {

    private String lotId;
    private String location;
    private Integer capacity;
    private Integer occupiedSpaces;
    private Integer availableSpaces;
    private BigDecimal costPerMinute;
}