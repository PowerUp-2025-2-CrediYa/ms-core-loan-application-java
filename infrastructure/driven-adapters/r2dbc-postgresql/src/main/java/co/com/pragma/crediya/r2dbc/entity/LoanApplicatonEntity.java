package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("loan.solicitudes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicatonEntity {

    @Id
    @Column("id_solicitud")
    private UUID loanApplicationId;

    @Column("documento_identidad")
    private String documentId;

    @Column("monto")
    private BigDecimal amount;

    @Column("tipo_prestamo")
    private String loanType;

    @Column("plazo_prestamo")
    private Integer loanTerm;

}
