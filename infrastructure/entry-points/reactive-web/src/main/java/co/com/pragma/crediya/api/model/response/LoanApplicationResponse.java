package co.com.pragma.crediya.api.model.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationResponse {

    private String documentId;
    private BigDecimal amount;
    private String loanType;
    private Integer loanTerm;
}
