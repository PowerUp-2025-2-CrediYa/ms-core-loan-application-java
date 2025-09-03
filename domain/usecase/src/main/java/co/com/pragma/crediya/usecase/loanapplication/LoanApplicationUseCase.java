package co.com.pragma.crediya.usecase.loanapplication;

import co.com.pragma.crediya.model.loanapplication.LoanApplication;
import co.com.pragma.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepositoryGateway;


    public Mono<LoanApplication> saveUser(LoanApplication loan) {
        return Mono.defer(() -> {

            return loanApplicationRepositoryGateway.saveLoanApplication(loan);
        });
    }

}
