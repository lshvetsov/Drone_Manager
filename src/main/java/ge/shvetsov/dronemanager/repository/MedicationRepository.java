package ge.shvetsov.dronemanager.repository;

import ge.shvetsov.dronemanager.model.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface MedicationRepository extends JpaRepository<MedicationEntity, String> {
    MedicationEntity findByCode(String code);
    List<MedicationEntity> findAllByCodeIn(List<String> codes);
    List<MedicationEntity> findAllByWeightLessThanEqual(BigDecimal weight);

}
