package ge.shvetsov.dronemanager.model.dto;

import ge.shvetsov.dronemanager.utilities.enums.DroneModel;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {
    private String id;

    @NotBlank
    private String serialNumber;

    @NotNull
    private DroneModel model;

    @Range(min = 0, max = 500)
    private BigDecimal weightLimit;

    @Range(min = 0, max = 100)
    private int batteryCapacity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DroneState state = DroneState.IDLE;

}