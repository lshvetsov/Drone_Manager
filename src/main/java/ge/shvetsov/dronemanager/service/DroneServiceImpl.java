package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.model.mapper.DroneMapper;
import ge.shvetsov.dronemanager.model.mapper.MedicationMapper;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.utilities.DroneManagerExceptionHandler;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    @Value("${app.chargeLevelLimit}")
    private Integer minChargingLevel;

    private final DroneRepository droneRepository;
    private final OrderService orderService;
    private final DroneMapper droneMapper;
    private final MedicationMapper medicationMapper;

    @Override
    public DroneDto registerNewProne(DroneDto drone) {
        DroneEntity droneEntity = droneMapper.toEntity(drone);
        droneEntity.setState(DroneState.IDLE);
        return droneMapper.toDto(droneRepository.save(droneEntity));
    }

    @Override
    public DroneDto getDrone(String droneId) {
        DroneEntity droneEntity = droneRepository.findById(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));
        return droneMapper.toDto(droneEntity);
    }

    @Override
    public List<DroneDto> getDrones() {
        return droneRepository.findAll().stream().map(droneMapper::toDto).toList();
    }

    @Override
    public boolean isDroneAvailable(String droneId) {
        Optional<DroneEntity> expectedDrone = droneRepository.findById(droneId);
        return expectedDrone.filter(drone -> Objects.equals(DroneState.IDLE, drone.getState())).isPresent();
    }

    @Override
    public Integer getDroneBattery(String droneId) {
        DroneEntity drone = droneRepository.findById(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));
        return drone.getBatteryCapacity();
    }

    @Override
    @Transactional
    public String loadDroneWithMedications(String droneId, List<MedicationDto> medication) {

        DroneEntity droneEntity = droneRepository.findById(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));

        this.validateConditionsForLoading(droneEntity, medication);
        this.switchDroneState(droneEntity, DroneState.LOADING);


        OrderDto order = orderService.saveOrder(
                OrderDto.builder()
                .number(UUID.randomUUID().toString())
                .customerName("Main client")
                .medications(medication)
                .drone(droneMapper.toDto(droneEntity))
                .build()
        );

        this.switchDroneState(droneEntity, DroneState.LOADED);

        return order.getNumber();
    }

    @Override
    public List<MedicationDto> getMedicationsForDrone(String droneId) {
        OrderEntity activeOrdersForDrone = orderService.getActiveOrdersForDrone(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS7"));
        return activeOrdersForDrone.getMedications().stream().map(medicationMapper::toDto).toList();
    }

    @Override
    public String launchDrone(String droneId) {
        DroneEntity drone = droneRepository.findById(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));

        if (!DroneState.LOADED.equals(drone.getState())) throw DroneManagerExceptionHandler.exceptions.get("MS5");

        this.switchDroneState(drone, DroneState.DELIVERING);

        OrderEntity order = orderService.getActiveOrdersForDrone(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS7"));
        orderService.switchOrderState(order, OrderState.SHIPPING);

        return order.getNumber();
    }

    @Override
    public DroneDto receiveDrone(String droneId) {
        DroneEntity drone = droneRepository.findById(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));

        boolean droneOnMission = DroneState.DELIVERED.equals(drone.getState()) || DroneState.RETURNING.equals(drone.getState())
                || DroneState.DELIVERING.equals(drone.getState());
        if (!droneOnMission) throw DroneManagerExceptionHandler.exceptions.get("MS6");

        this.switchDroneState(drone, DroneState.IDLE);

        OrderEntity order = orderService.getActiveOrdersForDrone(droneId).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS7"));
        orderService.switchOrderState(order, OrderState.DELIVERED);

        return droneMapper.toDto(drone);
    }

    @Override
    public void removeDrone(String droneId) {
        this.droneRepository.deleteById(droneId);
    }

    @Override
    public DroneDto updateDrone(DroneDto drone) {
        droneRepository.findById(drone.getId()).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS1"));
        DroneEntity droneEntity = droneRepository.save(droneMapper.toEntity(drone));
        return droneMapper.toDto(droneEntity);
    }

    @Override
    public void switchDroneState(DroneEntity drone, DroneState state) {
        drone.setState(state);
        droneRepository.save(drone);
    }

    private void validateConditionsForLoading(DroneEntity drone, List<MedicationDto> medications) {
        BigDecimal medicationWeight = medications.stream().map(MedicationDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!DroneState.IDLE.equals(drone.getState())) throw DroneManagerExceptionHandler.exceptions.get("MS4");
        if (drone.getBatteryCapacity() < minChargingLevel) throw DroneManagerExceptionHandler.exceptions.get("MS2");
        if (medicationWeight.compareTo(drone.getWeightLimit()) > 0) throw DroneManagerExceptionHandler.exceptions.get("MS3");
    }

    public void setMinChargingLevel(Integer minChargingLevel) {
        this.minChargingLevel = minChargingLevel;
    }
}
