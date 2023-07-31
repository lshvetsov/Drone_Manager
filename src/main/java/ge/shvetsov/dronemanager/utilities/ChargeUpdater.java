package ge.shvetsov.dronemanager.utilities;

import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import ge.shvetsov.dronemanager.repository.DroneRepository;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChargeUpdater {

    private final DroneRepository droneRepository;

    @Scheduled(fixedRate = 60000)
    public void updateChargeLevel() {
        List<DroneEntity> all = droneRepository.findAll();

        for (DroneEntity drone:all) {
            DroneState state = drone.getState();
            boolean droneAtCharging = DroneState.IDLE.equals(state) || DroneState.LOADING.equals(state) || DroneState.LOADED.equals(state);
            int batteryCapacity = drone.getBatteryCapacity();

            if (droneAtCharging && batteryCapacity == 100) continue;

            if (droneAtCharging)
                drone.setBatteryCapacity(batteryCapacity + 1);
            else
                drone.setBatteryCapacity(batteryCapacity - 1);

            log.info("Current change level of drone {} is {}%%", drone.getId(), drone.getBatteryCapacity());
            droneRepository.save(drone);
        }

    }

}
