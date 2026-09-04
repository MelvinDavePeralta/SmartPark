package com.smart.park.dto;

import com.smart.park.entity.VehicleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class VehicleRequest implements Serializable {

    @Pattern(message = "License plate can only contain letters, numbers, and dashes", regexp = "^[A-Za-z0-9-]+$")
    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @Pattern(message = "Owner name can only contain letters and spaces", regexp = "^[A-Za-z ]+$")
    @NotBlank(message = "Owner name is required")
    private String ownerName;
}