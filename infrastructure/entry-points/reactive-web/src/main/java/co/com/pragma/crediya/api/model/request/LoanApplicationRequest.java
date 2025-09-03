package co.com.pragma.crediya.api.model.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    private String documentId;
    private BigDecimal amount;
    private String loanType;
    private Integer loanTerm;

}
