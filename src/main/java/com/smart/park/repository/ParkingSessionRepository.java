package com.smart.park.repository;

import com.smart.park.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findByVehicleLicensePlateAndCheckOutTimeIsNull(String licensePlate);

    List<ParkingSession> findByCheckOutTimeIsNullAndCheckInTimeBefore(LocalDateTime time);

    List<ParkingSession> findByParkingLotLotIdAndCheckOutTimeIsNull(String lotId);

}