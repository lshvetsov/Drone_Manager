package ge.shvetsov.dronemanager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.shvetsov.dronemanager.utilities.enums.DroneModel;
import ge.shvetsov.dronemanager.utilities.enums.DroneState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DRONE")
public class DroneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "SERIAL_NUMBER", unique = true, nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "MODEL", nullable = false)
    private DroneModel model;

    @Column(name = "WEIGHT_LIMIT", nullable = false)
    private BigDecimal weightLimit;

    @Column(name = "BATTERY_CAPACITY", nullable = false)
    private int batteryCapacity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "STATE", nullable = false)
    private DroneState state = DroneState.IDLE;

    @OneToMany(mappedBy = "drone")
    @JsonIgnore
    private List<OrderEntity> OrderEntities;

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public void setState(DroneState state) {
        this.state = state;
    }

    public void setModel(DroneModel model) {
        this.model = model;
    }

}