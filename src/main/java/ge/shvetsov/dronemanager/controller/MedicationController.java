package ge.shvetsov.dronemanager.controller;

import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/medication")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService medicationService;

    @PostMapping
    public ResponseEntity<MedicationDto> createMedication(@RequestBody ge.shvetsov.dronemanager.model.dto.MedicationDto medication) {
        ge.shvetsov.dronemanager.model.dto.MedicationDto registeredMedication = this.medicationService.createMedication(medication);
        return new ResponseEntity<>(registeredMedication, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MedicationDto> updateMedication(@RequestBody ge.shvetsov.dronemanager.model.dto.MedicationDto medication) {
        ge.shvetsov.dronemanager.model.dto.MedicationDto updateMedication = medicationService.updateMedication(medication);
        return new ResponseEntity<>(updateMedication, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(@PathVariable String id) {
        medicationService.deleteMedication(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<MedicationDto> getMedication(@PathVariable String code) {
        ge.shvetsov.dronemanager.model.dto.MedicationDto medication = medicationService.getMedicationByCode(code);
        if (medication != null) {
            return new ResponseEntity<>(medication, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/list")
    public ResponseEntity<List<MedicationDto>> getMedications(@RequestBody List<String> codes) {
        List<ge.shvetsov.dronemanager.model.dto.MedicationDto> medications = medicationService.getMedications(codes);
        return new ResponseEntity<>(medications, HttpStatus.OK);
    }

    // Get medications with weight less than the given weight
    @GetMapping("/weight-less-than/{weight}")
    public ResponseEntity<List<MedicationDto>> getMedicationsLessThan(@PathVariable BigDecimal weight) {
        List<ge.shvetsov.dronemanager.model.dto.MedicationDto> medications = medicationService.getMedicationsLessThan(weight);
        return new ResponseEntity<>(medications, HttpStatus.OK);
    }


}
