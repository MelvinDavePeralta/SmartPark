package com.smart.park.service.impl;

import com.smart.park.dto.ParkedVehicleResponse;
import com.smart.park.dto.ParkingLotAvailabilityResponse;
import com.smart.park.dto.ParkingSessionRequest;
import com.smart.park.entity.ParkingLot;
import com.smart.park.entity.ParkingSession;
import com.smart.park.entity.Vehicle;
import com.smart.park.entity.VehicleType;
import com.smart.park.exception.BusinessException;
import com.smart.park.exception.ResourceNotFoundException;
import com.smart.park.mapper.ParkingSessionMapper;
import com.smart.park.repository.ParkingLotRepository;
import com.smart.park.repository.ParkingSessionRepository;
import com.smart.park.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSessionServiceImplTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingSessionMapper parkingSessionMapper;

    @InjectMocks
    private ParkingSessionServiceImpl parkingSessionService;

    private ParkingSessionRequest request;
    private Vehicle vehicle;
    private ParkingLot parkingLot;
    private ParkingSession session;

    @BeforeEach
    void setUp() {
        request = new ParkingSessionRequest();
        request.setLicensePlate("ABC-123");
        request.setLotId("LOT-A");

        vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");
        vehicle.setType(VehicleType.CAR);
        vehicle.setOwnerName("John Car");

        parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-A");
        parkingLot.setLocation("Downtown Plaza");
        parkingLot.setCapacity(50);
        parkingLot.setOccupiedSpaces(0);
        parkingLot.setCostPerMinute(new BigDecimal("0.25"));

        session = new ParkingSession();
        session.setId(1L);
        session.setVehicle(vehicle);
        session.setParkingLot(parkingLot);
        session.setCheckInTime(LocalDateTime.now().minusMinutes(10));
    }

    @Test
    void shouldCheckInVehicleSuccessfully() {
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.empty());
        when(parkingLotRepository.findByIdForUpdate("LOT-A")).thenReturn(Optional.of(parkingLot));
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSession result = parkingSessionService.checkIn(request);

        assertNotNull(result);
        assertEquals(vehicle, result.getVehicle());
        assertEquals(parkingLot, result.getParkingLot());
        assertNotNull(result.getCheckInTime());
        assertEquals(1, parkingLot.getOccupiedSpaces());

        verify(vehicleRepository).findById("ABC-123");
        verify(parkingSessionRepository).findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123");
        verify(parkingLotRepository).findByIdForUpdate("LOT-A");
        verify(parkingLotRepository).save(parkingLot);
        verify(parkingSessionRepository).save(any(ParkingSession.class));
    }

    @Test
    void shouldThrowExceptionWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.checkIn(request)
        );

        assertEquals("Vehicle not found", exception.getMessage());

        verify(vehicleRepository).findById("ABC-123");
        verifyNoInteractions(parkingSessionRepository, parkingLotRepository);
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsAlreadyCheckedIn() {
        ParkingSession activeSession = new ParkingSession();
        activeSession.setCheckInTime(LocalDateTime.of(2026, 9, 4, 20, 0));
        activeSession.setParkingLot(parkingLot);

        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.of(activeSession));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> parkingSessionService.checkIn(request)
        );

        assertTrue(exception.getMessage().contains("Vehicle is already checked in"));
        assertTrue(exception.getMessage().contains("LOT-A"));

        verify(vehicleRepository).findById("ABC-123");
        verify(parkingSessionRepository)
                .findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123");
        verifyNoInteractions(parkingLotRepository);
    }

    @Test
    void shouldThrowExceptionWhenParkingLotDoesNotExist() {
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.empty());
        when(parkingLotRepository.findByIdForUpdate("LOT-A")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.checkIn(request)
        );

        assertEquals("Parking lot not found", exception.getMessage());

        verify(parkingLotRepository).findByIdForUpdate("LOT-A");
        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenParkingLotIsFull() {
        parkingLot.setCapacity(10);
        parkingLot.setOccupiedSpaces(10);

        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.empty());
        when(parkingLotRepository.findByIdForUpdate("LOT-A")).thenReturn(Optional.of(parkingLot));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> parkingSessionService.checkIn(request)
        );

        assertEquals("Parking lot is full", exception.getMessage());

        verify(parkingLotRepository).findByIdForUpdate("LOT-A");
        verify(parkingLotRepository, never()).save(any());
        verify(parkingSessionRepository, never()).save(any());
    }

    @Test
    void shouldCheckOutVehicleSuccessfully() {
        session.setCheckInTime(LocalDateTime.now().minusMinutes(10));
        parkingLot.setOccupiedSpaces(1);

        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.of(session));
        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSession result = parkingSessionService.checkOut(request);

        assertNotNull(result);
        assertNotNull(result.getCheckOutTime());
        assertNotNull(result.getCost());
        assertTrue(result.getCost().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, parkingLot.getOccupiedSpaces());

        verify(parkingSessionRepository)
                .findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123");
        verify(parkingLotRepository).save(parkingLot);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsNotCheckedIn() {
        when(parkingSessionRepository.findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.checkOut(request)
        );

        assertEquals("Vehicle is not currently checked in", exception.getMessage());

        verify(parkingSessionRepository)
                .findByVehicleLicensePlateAndCheckOutTimeIsNull("ABC-123");
        verifyNoInteractions(parkingLotRepository);
    }

    @Test
    void shouldReturnParkingLotAvailability() {
        ParkingLotAvailabilityResponse response = new ParkingLotAvailabilityResponse(
                "LOT-A",
                "Downtown Plaza",
                50,
                10,
                40
        );

        parkingLot.setOccupiedSpaces(10);

        when(parkingLotRepository.findById("LOT-A")).thenReturn(Optional.of(parkingLot));
        when(parkingSessionMapper.toAvailabilityResponse(parkingLot)).thenReturn(response);

        ParkingLotAvailabilityResponse result =
                parkingSessionService.getParkingLotAvailability("LOT-A");

        assertNotNull(result);
        assertEquals("LOT-A", result.getLotId());
        assertEquals("Downtown Plaza", result.getLocation());
        assertEquals(50, result.getCapacity());
        assertEquals(10, result.getOccupiedSpaces());
        assertEquals(40, result.getAvailableSpaces());

        verify(parkingLotRepository).findById("LOT-A");
        verify(parkingSessionMapper).toAvailabilityResponse(parkingLot);
    }

    @Test
    void shouldThrowExceptionWhenAvailabilityParkingLotDoesNotExist() {
        when(parkingLotRepository.findById("LOT-A")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> parkingSessionService.getParkingLotAvailability("LOT-A")
        );

        assertEquals("Parking lot not found", exception.getMessage());

        verify(parkingLotRepository).findById("LOT-A");
        verifyNoInteractions(parkingSessionMapper);
    }

    @Test
    void shouldReturnCurrentlyParkedVehicles() {
        ParkingSession session2 = new ParkingSession();
        session2.setId(2L);
        session2.setVehicle(vehicle);
        session2.setParkingLot(parkingLot);
        session2.setCheckInTime(LocalDateTime.now().minusMinutes(5));

        ParkedVehicleResponse response = new ParkedVehicleResponse(
                "ABC-123",
                "John Car",
                "CAR",
                session.getCheckInTime()
        );

        when(parkingLotRepository.findById("LOT-A")).thenReturn(Optional.of(parkingLot));
        when(parkingSessionRepository.findByParkingLotLotIdAndCheckOutTimeIsNull("LOT-A"))
                .thenReturn(Arrays.asList(session, session2));
        when(parkingSessionMapper.toParkedVehicleResponse(session)).thenReturn(response);
        when(parkingSessionMapper.toParkedVehicleResponse(session2)).thenReturn(response);

        List<ParkedVehicleResponse> result =
                parkingSessionService.getCurrentParkedVehicles("LOT-A");

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(parkingLotRepository).findById("LOT-A");
        verify(parkingSessionRepository)
                .findByParkingLotLotIdAndCheckOutTimeIsNull("LOT-A");
        verify(parkingSessionMapper).toParkedVehicleResponse(session);
        verify(parkingSessionMapper).toParkedVehicleResponse(session2);
    }

    @Test
    void shouldReturnEmptyListWhenNoVehiclesAreParked() {
        when(parkingLotRepository.findById("LOT-A")).thenReturn(Optional.of(parkingLot));
        when(parkingSessionRepository.findByParkingLotLotIdAndCheckOutTimeIsNull("LOT-A"))
                .thenReturn(Collections.emptyList());

        List<ParkedVehicleResponse> result =
                parkingSessionService.getCurrentParkedVehicles("LOT-A");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(parkingLotRepository).findById("LOT-A");
        verify(parkingSessionRepository)
                .findByParkingLotLotIdAndCheckOutTimeIsNull("LOT-A");
        verifyNoInteractions(parkingSessionMapper);
    }

    @Test
    void shouldThrowExceptionWhenGettingParkedVehiclesForUnknownLot() {
        when(parkingLotRepository.findById("LOT-A")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parkingSessionService.getCurrentParkedVehicles("LOT-A")
        );

        assertEquals("Parking lot not found", exception.getMessage());

        verify(parkingLotRepository).findById("LOT-A");
        verifyNoInteractions(parkingSessionRepository, parkingSessionMapper);
    }

    @Test
    void shouldAutomaticallyRemoveExpiredVehicles() {
        session.setCheckInTime(LocalDateTime.now().minusMinutes(20));
        parkingLot.setOccupiedSpaces(1);

        when(parkingSessionRepository.findByCheckOutTimeIsNullAndCheckInTimeBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(session));
        when(parkingSessionRepository.save(any(ParkingSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        parkingSessionService.removeExpiredVehicles();

        assertNotNull(session.getCheckOutTime());
        assertNotNull(session.getCost());
        assertEquals(0, parkingLot.getOccupiedSpaces());

        verify(parkingSessionRepository)
                .findByCheckOutTimeIsNullAndCheckInTimeBefore(any(LocalDateTime.class));
        verify(parkingLotRepository).save(parkingLot);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void shouldDoNothingWhenThereAreNoExpiredVehicles() {
        when(parkingSessionRepository.findByCheckOutTimeIsNullAndCheckInTimeBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        parkingSessionService.removeExpiredVehicles();

        verify(parkingSessionRepository)
                .findByCheckOutTimeIsNullAndCheckInTimeBefore(any(LocalDateTime.class));
        verifyNoInteractions(parkingLotRepository);
    }
}