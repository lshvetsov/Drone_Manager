package ge.shvetsov.dronemanager.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {

    private String id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Name can only contain letters, numbers, '-', and '_'")
    private String name;
    private BigDecimal weight;
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code can only contain upper case letters, numbers, and '_'")
    private String code;
    private String image;
}
