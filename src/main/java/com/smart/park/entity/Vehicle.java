package com.smart.park.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "license_plate", length = 50, nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private String ownerName;
}