package com.smart.park.service.impl;

import com.smart.park.dto.VehicleRequest;
import com.smart.park.entity.Vehicle;
import com.smart.park.entity.VehicleType;
import com.smart.park.exception.BusinessException;
import com.smart.park.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequest request;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        request = new VehicleRequest();
        request.setLicensePlate("ABC-123");
        request.setType(VehicleType.CAR);
        request.setOwnerName("John Car");

        vehicle = new Vehicle();
        vehicle.setLicensePlate("ABC-123");
        vehicle.setType(VehicleType.CAR);
        vehicle.setOwnerName("John Car");
    }

    @Test
    void shouldRegisterVehicleSuccessfully() {
        when(vehicleRepository.existsById("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = vehicleService.registerVehicle(request);

        assertNotNull(result);
        assertEquals("ABC-123", result.getLicensePlate());
        assertEquals(VehicleType.CAR, result.getType());
        assertEquals("John Car", result.getOwnerName());

        verify(vehicleRepository).existsById("ABC-123");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenVehicleAlreadyExists() {
        when(vehicleRepository.existsById("ABC-123")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> vehicleService.registerVehicle(request)
        );

        assertEquals("Vehicle already exists", exception.getMessage());

        verify(vehicleRepository).existsById("ABC-123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldSaveVehicleWithCorrectDetails() {
        when(vehicleRepository.existsById("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = vehicleService.registerVehicle(request);

        assertEquals(request.getLicensePlate(), result.getLicensePlate());
        assertEquals(request.getType(), result.getType());
        assertEquals(request.getOwnerName(), result.getOwnerName());

        verify(vehicleRepository).save(any(Vehicle.class));
    }
}