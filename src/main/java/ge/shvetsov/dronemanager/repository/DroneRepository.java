package ge.shvetsov.dronemanager.repository;

import ge.shvetsov.dronemanager.model.entity.DroneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneRepository extends JpaRepository<DroneEntity, String> {
    // Add custom query methods if needed
}
