package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.MedicationDto;

import java.math.BigDecimal;
import java.util.List;

public interface MedicationService {

    MedicationDto getMedicationById(String code);
    MedicationDto getMedicationByCode(String code);
    List<MedicationDto> getMedications(List<String> codes);
    List<MedicationDto> getMedicationsLessThan(BigDecimal weight);
    MedicationDto createMedication(MedicationDto medication);
    MedicationDto updateMedication(MedicationDto medication);
    void deleteMedication(String medicationId);

}
