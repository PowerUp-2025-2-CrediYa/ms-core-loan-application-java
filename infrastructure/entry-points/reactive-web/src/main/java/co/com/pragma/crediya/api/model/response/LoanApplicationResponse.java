package co.com.pragma.crediya.api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta al crear una solicitud")
public class LoanApplicationResponse {

    @Schema(description = "ID único de la solicitud ", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID loanApplicationId;

    @Schema(description = "Documento de identidad", example = "123456789")
    private String documentId;

    @Schema(description = "Monto", example = "50000000")
    private BigDecimal amount;

    @Schema(description = "Tipo de prestamo", example = "HIPOTECARIO")
    private String loanType;

    @Schema(description = "Plazo en meses", example = "24")
    private Integer loanTerm;

    @Schema(description = "Estado de la solicitud", example = "PEND_REV")
    private String loanStatus;
}
