package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.model.request.LoanApplicationRequest;
import co.com.pragma.crediya.model.loanapplication.LoanApplication;

public class LoanMapper {

    LoanMapper(){}

    public static LoanApplication toDomain(LoanApplicationRequest dto) {
        return LoanApplication.builder()
                .documentId(dto.getDocumentId())
                .amount(dto.getAmount())
                .loanType(dto.getLoanType())
                .loanTerm(dto.getLoanTerm())
                .build();
    }

}
