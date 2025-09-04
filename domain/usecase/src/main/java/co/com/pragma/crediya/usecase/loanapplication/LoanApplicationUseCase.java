package co.com.pragma.crediya.usecase.loanapplication;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.LoanStatusCode;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRestClient;
import co.com.pragma.crediya.model.loanapplication.helper.LoanApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepositoryGateway;
    private final LoanApplicationRestClient loanRestClientGateway;

    public Mono<LoanApplication> saveUser(LoanApplication loan) {

        return loanRestClientGateway.findUserByDocumentId(loan.getDocumentId())
                .flatMap(user -> {
                    LoanApplicationValidator.validate(loan);
                    loan.setLoanStatus(LoanStatusCode.PENDING_REVIEW);
                    return loanApplicationRepositoryGateway.saveLoanApplication(loan);
                });

    }

}
