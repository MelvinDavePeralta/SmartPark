package com.smart.park.service.impl;

import com.smart.park.dto.ParkingLotRequest;
import com.smart.park.dto.ParkingLotResponse;
import com.smart.park.entity.ParkingLot;
import com.smart.park.exception.BusinessException;
import com.smart.park.mapper.ParkingLotMapper;
import com.smart.park.repository.ParkingLotRepository;
import com.smart.park.service.ParkingLotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;

    public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository, ParkingLotMapper parkingLotMapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
    }


    @Override
    public ParkingLotResponse registerParkingLot(ParkingLotRequest parkingLotRequest) {
        log.info("Start register parking lot : {}", parkingLotRequest.getLotId());
        if (parkingLotRepository.existsById(parkingLotRequest.getLotId())) {
            throw new BusinessException("Parking lot already exists");
        }
        ParkingLot parkingLot = parkingLotMapper.toEntity(parkingLotRequest);
        parkingLot = parkingLotRepository.save(parkingLot);
        log.info("Parking lot has been saved : {}", parkingLot.getLotId());
        return parkingLotMapper.toResponse(parkingLot);
    }

    @Override
    public List<ParkingLotResponse> showAllParkingLot() {
        return parkingLotRepository.findAll()
                .stream()
                .map(parkingLotMapper::toResponse)
                .collect(Collectors.toList());
    }

}
