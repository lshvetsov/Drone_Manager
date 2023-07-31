package ge.shvetsov.dronemanager.model.entity;

import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ORDER_NUMBER", nullable = false, unique = true)
    private String number;

    @Column(name = "CUSTOMER_NAME", nullable = false)
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private DroneEntity drone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ORDER_STATE", nullable = false)
    private OrderState state = OrderState.READY;

    @ManyToMany
    @JoinTable(
            name = "order_medication",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private List<MedicationEntity> medications;

    public void setDrone(DroneEntity drone) {
        this.drone = drone;
    }
    public void setState(OrderState state) {
        this.state = state;
    }
    public void setMedications(List<MedicationEntity> medications) {
        this.medications = medications;
    }
}
