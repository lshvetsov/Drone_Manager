package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import ge.shvetsov.dronemanager.utilities.enums.DroneModel;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;

import java.math.BigDecimal;
import java.util.UUID;

public class TestUtilities {

    private static final String id = UUID.randomUUID().toString();
    private static final String number = UUID.randomUUID().toString();

    public static DroneEntity getDroneEntity() {
        return DroneEntity.builder()
                .id(id)
                .serialNumber(number)
                .model(DroneModel.LIGHTWEIGHT)
                .weightLimit(BigDecimal.valueOf(120L))
                .batteryCapacity(100)
                .state(DroneState.IDLE)
                .build();
    }

    public static OrderEntity getOrderEntity(DroneEntity drone) {

        return OrderEntity.builder()
                .id(id)
                .number(number)
                .customerName("Main client")
                .drone(drone)
                .build();
    }

    public static MedicationEntity getMedicationEntity() {
        return MedicationEntity.builder()
                .id(id)
                .name("Name")
                .code("RTYUII543")
                .weight(BigDecimal.valueOf(50L))
                .build();
    }

    public static DroneDto getDroneDto() {
        return DroneDto.builder()
                .id(id)
                .serialNumber(number)
                .model(DroneModel.LIGHTWEIGHT)
                .weightLimit(BigDecimal.valueOf(120L))
                .batteryCapacity(100)
                .state(DroneState.IDLE)
                .build();
    }

    public static OrderDto getOrderDto(DroneDto drone) {

        return OrderDto.builder()
                .id(id)
                .number(number)
                .customerName("Main client")
                .drone(drone)
                .build();
    }

    public static MedicationDto getMedicationDto() {
        return MedicationDto.builder()
                .id(id)
                .name("Name")
                .code("RTYUII543")
                .weight(BigDecimal.valueOf(50L))
                .build();
    }

}
