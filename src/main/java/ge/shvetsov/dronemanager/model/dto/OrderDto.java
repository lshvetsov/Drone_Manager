package ge.shvetsov.dronemanager.model.dto;

import ge.shvetsov.dronemanager.utilities.enums.OrderState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;
    @NotBlank
    private String number;
    @NotBlank
    private String customerName;
    @NotNull
    private DroneDto drone;
    @Builder.Default
    @NotNull
    private OrderState state = OrderState.READY;
    @NotNull
    private List<MedicationDto> medications;


}
