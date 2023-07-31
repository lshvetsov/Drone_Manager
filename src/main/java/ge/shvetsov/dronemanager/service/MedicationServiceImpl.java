package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import ge.shvetsov.dronemanager.model.mapper.MedicationMapper;
import ge.shvetsov.dronemanager.repository.MedicationRepository;
import ge.shvetsov.dronemanager.utilities.DroneManagerExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Override
    public MedicationDto getMedicationById(String id) {
        MedicationEntity medicationEntity = medicationRepository.findById(id).orElseThrow(() -> DroneManagerExceptionHandler.exceptions.get("MS8"));
        return medicationMapper.toDto(medicationEntity);
    }

    @Override
    public MedicationDto getMedicationByCode(String code) {
        MedicationEntity medicationEntity = medicationRepository.findByCode(code);
        return medicationMapper.toDto(medicationEntity);
    }

    @Override
    public List<MedicationDto> getMedications(List<String> codes) {
        return medicationRepository.findAllByCodeIn(codes).stream().map(medicationMapper::toDto).toList();
    }

    @Override
    public List<MedicationDto> getMedicationsLessThan(BigDecimal weight) {
        return medicationRepository.findAllByWeightLessThanEqual(weight).stream().map(medicationMapper::toDto).toList();
    }

    @Override
    public MedicationDto createMedication(ge.shvetsov.dronemanager.model.dto.MedicationDto medication) {
        MedicationEntity medicationEntity = this.medicationRepository.save(medicationMapper.toEntity(medication));
        return medicationMapper.toDto(medicationEntity);

    }

    @Override
    public MedicationDto updateMedication(MedicationDto medication) {
        Optional<MedicationEntity> byId = this.medicationRepository.findById(medication.getId());
        if (byId.isPresent()) {
            MedicationEntity medicationEntity = this.medicationRepository.save(medicationMapper.toEntity(medication));
            return medicationMapper.toDto(medicationEntity);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteMedication(String medicationId) {
        this.medicationRepository.deleteById(medicationId);
    }
}
