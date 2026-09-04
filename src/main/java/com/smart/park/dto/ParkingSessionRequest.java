package com.smart.park.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSessionRequest {

    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "License plate can only contain letters, numbers, and dashes")
    @NotBlank(message = "License Plate is required")
    private String licensePlate;

    @NotBlank(message = "Lot ID is required")
    @Size(max = 50, message = "Lot ID must not exceed 50 characters")
    private String lotId;

}
