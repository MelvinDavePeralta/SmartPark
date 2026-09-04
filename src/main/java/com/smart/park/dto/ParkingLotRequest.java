package com.smart.park.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ParkingLotRequest implements Serializable {

    @NotBlank(message = "Lot ID is required")
    @Size(max = 50, message = "Lot ID must not exceed 50 characters")
    private String lotId;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be a positive number")
    private Integer capacity;

    @NotNull(message = "Cost per minute is required")
    @DecimalMin(value = "0.01", message = "Cost per minute must be greater than 0")
    private BigDecimal costPerMinute;
}