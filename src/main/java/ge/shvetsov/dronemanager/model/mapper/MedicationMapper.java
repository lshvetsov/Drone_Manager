package ge.shvetsov.dronemanager.model.mapper;

import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import org.springframework.stereotype.Service;

@Service
public class MedicationMapper {

    public ge.shvetsov.dronemanager.model.dto.MedicationDto toDto(MedicationEntity entity) {
        if (entity == null) {
            return null;
        }
        return ge.shvetsov.dronemanager.model.dto.MedicationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .weight(entity.getWeight())
                .code(entity.getCode())
                .image(entity.getImage())
                .build();
    }

    public MedicationEntity toEntity(ge.shvetsov.dronemanager.model.dto.MedicationDto dto) {
        if (dto == null) {
            return null;
        }
        return MedicationEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .weight(dto.getWeight())
                .code(dto.getCode())
                .image(dto.getImage())
                .build();
    }

}
