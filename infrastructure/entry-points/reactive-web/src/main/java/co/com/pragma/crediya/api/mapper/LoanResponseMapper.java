package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.model.response.LoanApplicationResponse;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;

public class LoanResponseMapper {

    LoanResponseMapper(){}

    public static LoanApplicationResponse fromDomain(LoanApplication loanApplication){

return LoanApplicationResponse.builder()
        .loanApplicationId(loanApplication.getLoanApplicationId())
        .documentId(loanApplication.getDocumentId())
        .amount(loanApplication.getAmount())
        .loanType(loanApplication.getLoanType())
        .loanTerm(loanApplication.getLoanTerm())
        .loanStatus(loanApplication.getLoanStatus())
        .build();
    }

}
