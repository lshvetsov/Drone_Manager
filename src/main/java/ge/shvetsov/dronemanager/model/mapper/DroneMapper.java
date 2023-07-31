package ge.shvetsov.dronemanager.model.mapper;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import org.springframework.stereotype.Service;

@Service
public class DroneMapper {

    public DroneDto toDto(DroneEntity entity) {
        if (entity == null) {
            return null;
        }
        return DroneDto.builder()
                .id(entity.getId())
                .serialNumber(entity.getSerialNumber())
                .model(entity.getModel())
                .weightLimit(entity.getWeightLimit())
                .batteryCapacity(entity.getBatteryCapacity())
                .state(entity.getState())
                .build();
    }

    public DroneEntity toEntity(DroneDto dto) {
        if (dto == null) {
            return null;
        }
        return DroneEntity.builder()
                .id(dto.getId())
                .serialNumber(dto.getSerialNumber())
                .model(dto.getModel())
                .weightLimit(dto.getWeightLimit())
                .batteryCapacity(dto.getBatteryCapacity())
                .state(dto.getState())
                .build();
    }

}
