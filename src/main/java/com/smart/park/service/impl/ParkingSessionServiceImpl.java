package com.smart.park.service.impl;

import com.smart.park.dto.ParkedVehicleResponse;
import com.smart.park.dto.ParkingLotAvailabilityResponse;
import com.smart.park.dto.ParkingSessionRequest;
import com.smart.park.entity.ParkingLot;
import com.smart.park.entity.ParkingSession;
import com.smart.park.entity.Vehicle;
import com.smart.park.exception.BusinessException;
import com.smart.park.exception.ResourceNotFoundException;
import com.smart.park.mapper.ParkingSessionMapper;
import com.smart.park.repository.ParkingLotRepository;
import com.smart.park.repository.ParkingSessionRepository;
import com.smart.park.repository.VehicleRepository;
import com.smart.park.service.ParkingSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParkingSessionServiceImpl implements ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionMapper parkingSessionMapper;

    public ParkingSessionServiceImpl(ParkingSessionRepository parkingSessionRepository,
                                     ParkingLotRepository parkingLotRepository,
                                     VehicleRepository vehicleRepository,
                                     ParkingSessionMapper parkingSessionMapper) {

        this.parkingSessionRepository = parkingSessionRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSessionMapper = parkingSessionMapper;
    }

    @Override
    @Transactional
    public ParkingSession checkIn(ParkingSessionRequest request) {
        log.info("Start check in for vehicle {}", request.getLicensePlate());
        Vehicle vehicle = vehicleRepository.findById(request.getLicensePlate())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        Optional<ParkingSession> activeSession =
                parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull(vehicle.getLicensePlate());
        if (activeSession.isPresent()) {
            ParkingSession session = activeSession.get();
            throw new BusinessException(
                    "Vehicle is already checked in at: " + session.getCheckInTime() + " in parking lot: " + session.getParkingLot().getLotId());
        }

        ParkingLot parkingLot = parkingLotRepository.findByIdForUpdate(request.getLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found"));
        if (parkingLot.getOccupiedSpaces() >= parkingLot.getCapacity()) {
            throw new BusinessException("Parking lot is full");
        }

        ParkingSession session = new ParkingSession();
        session.setVehicle(vehicle);
        session.setParkingLot(parkingLot);
        session.setCheckInTime(LocalDateTime.now());

        parkingLot.setOccupiedSpaces(parkingLot.getOccupiedSpaces() + 1);
        parkingLotRepository.save(parkingLot);

        log.info("Vehicle {} checked in to parking lot {}", vehicle.getLicensePlate(), parkingLot.getLotId());

        return parkingSessionRepository.save(session);
    }

    @Override
    @Transactional
    public ParkingSession checkOut(ParkingSessionRequest request) {
        log.info("Start check out for vehicle {}", request.getLicensePlate());
        ParkingSession session = parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull(request.getLicensePlate())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle is not currently checked in"));
        return completeCheckout(session);
    }

    @Override
    public ParkingLotAvailabilityResponse getParkingLotAvailability(String lotId) {
        log.info("Get Parking Lot Available");
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found"));
        return parkingSessionMapper.toAvailabilityResponse(parkingLot);
    }

    @Override
    public List<ParkedVehicleResponse> getCurrentParkedVehicles(String lotId) {
        log.info("Get Currently Parked Vehicle");
        parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));
        return parkingSessionRepository.findByParkingLotLotIdAndCheckOutTimeIsNull(lotId)
                .stream()
                .map(parkingSessionMapper::toParkedVehicleResponse)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void removeExpiredVehicles() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<ParkingSession> expiredSessions =
                parkingSessionRepository.findByCheckOutTimeIsNullAndCheckInTimeBefore(cutoffTime);
        for (ParkingSession session : expiredSessions) {
            completeCheckout(session);
            log.info("Vehicle {} was automatically removed from parking lot {} after exceeding 15 minutes",
                    session.getVehicle().getLicensePlate(),
                    session.getParkingLot().getLotId()
            );
        }
    }

    private ParkingSession completeCheckout(ParkingSession session) {
        LocalDateTime checkOutTime = LocalDateTime.now();
        long minutesParked = Duration.between(session.getCheckInTime(), checkOutTime).toMinutes();
        long billableMinutes = Math.max(minutesParked, 1);
        BigDecimal cost = session.getParkingLot().getCostPerMinute().multiply(BigDecimal.valueOf(billableMinutes));

        session.setCheckOutTime(checkOutTime);
        session.setCost(cost);
        ParkingLot parkingLot = session.getParkingLot();
        parkingLot.setOccupiedSpaces(Math.max(0, parkingLot.getOccupiedSpaces() - 1));
        parkingLotRepository.save(parkingLot);
        return parkingSessionRepository.save(session);
    }
}