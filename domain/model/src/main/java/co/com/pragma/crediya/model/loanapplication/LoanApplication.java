package co.com.pragma.crediya.model.loanapplication;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

    private UUID loanApplicationId;
    private String documentId;
    private BigDecimal amount;
    private String loanType;
    private Integer loanTerm;
    private LocalDate createAt;
}
