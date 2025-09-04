package co.com.pragma.crediya.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de entrada para crear una solicitud de prestamo")
public class LoanApplicationRequest {

    @Schema(description = "Documento de identidad", example = "123456789")
    private String documentId;

    @Schema(description = "Monto", example = "50000000")
    private BigDecimal amount;

    @Schema(description = "Tipo de prestamo", example = "HIPOTECARIO")
    private String loanType;

    @Schema(description = "Plazo en meses", example = "24")
    private Integer loanTerm;

}
