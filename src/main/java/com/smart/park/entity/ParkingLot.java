package com.smart.park.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "parking_lot")
@Getter
@Setter
@NoArgsConstructor
public class ParkingLot {

    @Id
    @Column(name = "lot_id", length = 50, nullable = false, unique = true)
    private String lotId;

    private String location;

    private Integer capacity;

    private Integer occupiedSpaces;

    @Column(precision = 10, scale = 2)
    private BigDecimal costPerMinute;
}