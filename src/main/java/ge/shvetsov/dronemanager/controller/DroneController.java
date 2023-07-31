package ge.shvetsov.dronemanager.controller;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drones")
@RequiredArgsConstructor
public class DroneController {
    private final DroneService droneService;

    @PostMapping
    public ResponseEntity<DroneDto> registerNewDrone(@RequestBody DroneDto drone) {
        DroneDto registeredDrone = droneService.registerNewProne(drone);
        return new ResponseEntity<>(registeredDrone, HttpStatus.CREATED);
    }

    @GetMapping("/{droneId}")
    public ResponseEntity<DroneDto> getDrone(@PathVariable String droneId) {
        DroneDto drone = droneService.getDrone(droneId);
        if (drone != null) {
            return new ResponseEntity<>(drone, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<DroneDto> updateDrone(@RequestBody DroneDto drone) {
        DroneDto updateDrone = droneService.updateDrone(drone);
        return new ResponseEntity<>(updateDrone, HttpStatus.OK);
    }

    @DeleteMapping("/{droneId}")
    public ResponseEntity<DroneEntity> removeDrone(@PathVariable String droneId) {
        droneService.removeDrone(droneId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DroneDto>> getAllDrones() {
        List<DroneDto> drones = droneService.getDrones();
        return new ResponseEntity<>(drones, HttpStatus.OK);
    }

    @GetMapping("/{droneId}/available")
    public ResponseEntity<Boolean> isDroneAvailable(@PathVariable String droneId) {
        boolean isAvailable = droneService.isDroneAvailable(droneId);
        return new ResponseEntity<>(isAvailable ? HttpStatus.OK : HttpStatus.LOCKED);
    }

    // Get the battery level of a drone
    @GetMapping("/{droneId}/battery")
    public ResponseEntity<Integer> getDroneBattery(@PathVariable String droneId) {
        Integer batteryLevel = droneService.getDroneBattery(droneId);
        return new ResponseEntity<>(batteryLevel, HttpStatus.OK);
    }

    // Load a drone with medications
    @PostMapping("/{droneId}/load")
    public ResponseEntity<String> loadDroneWithMedications(@PathVariable String droneId,
                                                            @RequestBody List<MedicationDto> medications) {
        String orderNum = droneService.loadDroneWithMedications(droneId, medications);
        return new ResponseEntity<>(orderNum, HttpStatus.OK);
    }

    // Get medications loaded in a drone
    @GetMapping("/{droneId}/medications")
    public ResponseEntity<List<MedicationDto>> getMedicationsForDrone(@PathVariable String droneId) {
        List<MedicationDto> medications = droneService.getMedicationsForDrone(droneId);
        return new ResponseEntity<>(medications, HttpStatus.OK);
    }

    // Launch a drone
    @PostMapping("/{droneId}/launch")
    public ResponseEntity<String> launchDrone(@PathVariable String droneId) {
        String orderNum = droneService.launchDrone(droneId);
        return new ResponseEntity<>(orderNum, HttpStatus.OK);
    }

    // Receive a drone after delivery
    @PostMapping("/{droneId}/receive")
    public ResponseEntity<DroneDto> receiveDrone(@PathVariable String droneId) {
        DroneDto received = droneService.receiveDrone(droneId);
        return new ResponseEntity<>(received, HttpStatus.OK);
    }

}
