package com.smart.park.service.impl;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.ParkingLotResponse;
import com.smart.park.entity.ParkingLot;
import com.smart.park.exception.BusinessException;
import com.smart.park.mapper.ParkingLotMapper;
import com.smart.park.repository.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private ParkingLotMapper parkingLotMapper;

    @InjectMocks
    private ParkingLotServiceImpl parkingLotService;

    private ParkingLotRequest request;
    private ParkingLot parkingLot;
    private ParkingLotResponse response;

    @BeforeEach
    void setUp() {
        request = new ParkingLotRequest();
        request.setLotId("LOT-A");
        request.setLocation("Makati");
        request.setCapacity(50);
        request.setCostPerMinute(new BigDecimal("1"));

        parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-A");
        parkingLot.setLocation("Makati");
        parkingLot.setCapacity(50);
        parkingLot.setOccupiedSpaces(0);
        parkingLot.setCostPerMinute(new BigDecimal("1"));

        response = new ParkingLotResponse();
        response.setLotId("LOT-A");
        response.setLocation("Makati");
        response.setCapacity(50);
        response.setOccupiedSpaces(0);
        response.setCostPerMinute(new BigDecimal("1"));
    }

    @Test
    void shouldRegisterParkingLotSuccessfully() {
        when(parkingLotRepository.existsById("LOT-A")).thenReturn(false);
        when(parkingLotMapper.toEntity(request)).thenReturn(parkingLot);
        when(parkingLotRepository.save(parkingLot)).thenReturn(parkingLot);
        when(parkingLotMapper.toResponse(parkingLot)).thenReturn(response);

        ParkingLotResponse result = parkingLotService.registerParkingLot(request);

        assertNotNull(result);
        assertEquals("LOT-A", result.getLotId());
        assertEquals("Makati", result.getLocation());
        assertEquals(50, result.getCapacity());
        assertEquals(0, result.getOccupiedSpaces());
        assertEquals(new BigDecimal("1"), result.getCostPerMinute());

        verify(parkingLotRepository).existsById("LOT-A");
        verify(parkingLotMapper).toEntity(request);
        verify(parkingLotRepository).save(parkingLot);
        verify(parkingLotMapper).toResponse(parkingLot);
    }

    @Test
    void shouldThrowExceptionWhenParkingLotAlreadyExists() {
        when(parkingLotRepository.existsById("LOT-A")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> parkingLotService.registerParkingLot(request)
        );

        assertEquals("Parking lot already exists", exception.getMessage());

        verify(parkingLotRepository).existsById("LOT-A");
        verify(parkingLotMapper, never()).toEntity(any());
        verify(parkingLotRepository, never()).save(any());
        verify(parkingLotMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnAllParkingLots() {
        ParkingLot parkingLot2 = new ParkingLot();
        parkingLot2.setLotId("LOT-B");
        parkingLot2.setLocation("Airport Terminal 1");
        parkingLot2.setCapacity(100);
        parkingLot2.setOccupiedSpaces(10);
        parkingLot2.setCostPerMinute(new BigDecimal("0.50"));

        ParkingLotResponse response2 = new ParkingLotResponse();
        response2.setLotId("LOT-B");
        response2.setLocation("Airport Terminal 1");
        response2.setCapacity(100);
        response2.setOccupiedSpaces(10);
        response2.setCostPerMinute(new BigDecimal("0.50"));

        when(parkingLotRepository.findAll())
                .thenReturn(Arrays.asList(parkingLot, parkingLot2));
        when(parkingLotMapper.toResponse(parkingLot))
                .thenReturn(response);
        when(parkingLotMapper.toResponse(parkingLot2))
                .thenReturn(response2);

        List<ParkingLotResponse> result = parkingLotService.showAllParkingLot();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("LOT-A", result.get(0).getLotId());
        assertEquals("LOT-B", result.get(1).getLotId());

        verify(parkingLotRepository).findAll();
        verify(parkingLotMapper).toResponse(parkingLot);
        verify(parkingLotMapper).toResponse(parkingLot2);
    }

    @Test
    void shouldReturnEmptyListWhenNoParkingLotsExist() {
        when(parkingLotRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<ParkingLotResponse> result = parkingLotService.showAllParkingLot();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(parkingLotRepository).findAll();
        verifyNoInteractions(parkingLotMapper);
    }
}