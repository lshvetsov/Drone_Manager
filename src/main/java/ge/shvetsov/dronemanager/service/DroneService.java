package ge.shvetsov.dronemanager.service;

import ge.shvetsov.dronemanager.model.dto.DroneDto;
import ge.shvetsov.dronemanager.model.dto.MedicationDto;
import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;

import java.util.List;

public interface DroneService {

    DroneDto registerNewProne(DroneDto drone);
    DroneDto getDrone(String droneId);
    List<DroneDto> getDrones();
    boolean isDroneAvailable(String droneId);
    Integer getDroneBattery(String droneId);
    String loadDroneWithMedications(String droneId, List<MedicationDto> medications);
    List<MedicationDto> getMedicationsForDrone(String droneId);
    String launchDrone(String droneId);
    DroneDto receiveDrone(String droneId);
    void removeDrone(String droneId);
    DroneDto updateDrone(DroneDto drone);
    void switchDroneState(DroneEntity drone, DroneState state);
}
