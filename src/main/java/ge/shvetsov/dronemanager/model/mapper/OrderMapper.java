package ge.shvetsov.dronemanager.model.mapper;

import ge.shvetsov.dronemanager.model.dto.OrderDto;
import ge.shvetsov.dronemanager.model.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMapper {

    private final DroneMapper droneMapper;
    private final MedicationMapper medicationMapper;

    public OrderDto toDto(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        return OrderDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .customerName(entity.getCustomerName())
                .drone(droneMapper.toDto(entity.getDrone()))
                .state(entity.getState())
                .medications(entity.getMedications().stream().map(medicationMapper::toDto).toList())
                .build();
    }

    public OrderEntity toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }
        return OrderEntity.builder()
                .id(dto.getId())
                .number(dto.getNumber())
                .customerName(dto.getCustomerName())
                .drone(droneMapper.toEntity(dto.getDrone()))
                .state(dto.getState())
                .medications(dto.getMedications().stream().map(medicationMapper::toEntity).toList())
                .build();
    }



}
